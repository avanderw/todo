package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.SneakyThrows;
import net.avdw.todo.domain.IsDone;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import org.junit.*;
import org.junit.rules.Timeout;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

public class DoneCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/done/.todo/todo.txt");
    private static CommandLine commandLine;
    private final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private StringWriter errWriter;
    private StringWriter outWriter;

    @BeforeClass
    @SneakyThrows
    public static void warmup() {
        Files.createDirectories(todoPath.getParent());
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), todoPath, StandardCopyOption.REPLACE_EXISTING);
        commandLine = new CommandLine(RefactoredMainCli.class, new GuiceFactory(new TestModule()));
        commandLine.execute("");
    }

    @Before
    @SneakyThrows
    public void beforeTest() {
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), todoPath, StandardCopyOption.REPLACE_EXISTING);
        commandLine = new CommandLine(RefactoredMainCli.class, new GuiceFactory(new TestModule()));
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));
    }

    @AfterClass
    @SneakyThrows
    public static void afterClass() {
        Files.deleteIfExists(todoPath);
        Files.deleteIfExists(todoPath.getParent());
        Files.deleteIfExists(todoPath.getParent().getParent());
    }

    @Test(timeout = 50)
    public void testNoIdx() {
        assertFailure(commandLine.execute("do"));
    }

    private void assertFailure(final int exitCode) {
        if (!outWriter.toString().isEmpty()) {
            Logger.debug("Standard output:\n{}", outWriter.toString());
        }
        if (!errWriter.toString().isEmpty()) {
            Logger.debug("Error output:\n{}", errWriter.toString());
        }
        assertNotEquals("MUST HAVE error output", "", errWriter.toString());
        assertEquals("MUST NOT HAVE standard output", "", outWriter.toString().trim());
        assertNotEquals(0, exitCode);
    }

    @Test(timeout = 50)
    public void testPriorityRemoval() {
        assertSuccess(commandLine.execute("pri 7 A".split(" ")));
        resetOutput();
        assertSuccess(commandLine.execute("do 7".split(" ")));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new IsDone());
        assertEquals(1, doneTodoList.size());
        assertFalse(doneTodoList.get(0).getText().contains("(A)"));
    }

    @Test(timeout = 50)
    public void testRepeatIdx() {
        assertSuccess(commandLine.execute("do 5,5".split(" ")));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new IsDone());
        assertEquals(1, doneTodoList.size());
        assertFalse(doneTodoList.get(0).getText().startsWith(String.format("x %s x ", SIMPLE_DATE_FORMAT.format(new Date()))));
    }

    private void assertSuccess(final int exitCode) {
        if (!outWriter.toString().isEmpty()) {
            Logger.debug("Standard output:\n{}", outWriter.toString());
        }
        if (!errWriter.toString().isEmpty()) {
            Logger.error("Error output:\n{}", errWriter.toString());
        }
        assertEquals("MUST NOT HAVE error output", "", errWriter.toString());
        assertNotEquals("MUST HAVE standard output", "", outWriter.toString().trim());
        assertEquals(0, exitCode);
    }

    @Test(timeout = 50)
    public void testOneIdx() {
        assertSuccess(commandLine.execute("do 2".split(" ")));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new IsDone());
        assertEquals(1, doneTodoList.size());
        assertTrue(doneTodoList.get(0).getText().startsWith(String.format("x %s 2019-02-07", SIMPLE_DATE_FORMAT.format(new Date()))));
    }

    @Test(timeout = 50)
    public void testTwoIdx() {
        assertSuccess(commandLine.execute("do 2,4".split(" ")));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new IsDone());
        assertEquals(2, doneTodoList.size());
    }

    private void resetOutput() {
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));
    }


    static class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(List.class).to(LinkedList.class);
            bind(Set.class).to(HashSet.class);
            bind(Path.class).toInstance(todoPath);
            bind(ResourceBundle.class).toInstance(ResourceBundle.getBundle("messages", Locale.getDefault()));
        }

        @Provides
        @Singleton
        Repository<Integer, Todo> todoRepository(final Path todoPath) {
            return new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        }
    }
}
