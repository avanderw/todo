package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.SneakyThrows;
import net.avdw.todo.domain.IsRemoved;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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

public class RemoveCliTest {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Path todoPath = Paths.get("target/test-resources/remove/.todo/todo.txt");
    private static CommandLine commandLine;
    private StringWriter errWriter;
    private StringWriter outWriter;

    @Before
    @SneakyThrows
    public void beforeTest() {
        Files.createDirectories(todoPath.getParent());
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), todoPath, StandardCopyOption.REPLACE_EXISTING);
        commandLine = new CommandLine(RefactoredMainCli.class, new GuiceFactory(new TestModule()));
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));
    }

    @After
    @SneakyThrows
    public void afterTest() {
        Files.deleteIfExists(todoPath);
        Files.deleteIfExists(todoPath.getParent());
        Files.deleteIfExists(todoPath.getParent().getParent());
    }

    @Test(timeout = 50)
    public void testNoIdx() {
        assertFailure(commandLine.execute("rm"));
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
    public void testRepeatIdx() {
        assertSuccess(commandLine.execute("rm 5,5".split(" ")));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> removedTodoList = todoRepository.findAll(new IsRemoved());
        assertEquals(1, removedTodoList.size());
        assertFalse(removedTodoList.get(0).getText().startsWith(String.format("r %s r ", SIMPLE_DATE_FORMAT.format(new Date()))));
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
    public void testPriorityRemoval() {
        assertSuccess(commandLine.execute("pri 7 A".split(" ")));
        resetOutput();
        assertSuccess(commandLine.execute("rm 7".split(" ")));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> removedTodoList = todoRepository.findAll(new IsRemoved());
        assertEquals(1, removedTodoList.size());
        assertFalse(removedTodoList.get(0).getText().contains("(A)"));
    }

    private void resetOutput() {
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));
    }

    @Test(timeout = 50)
    public void testOneIdx() {
        assertSuccess(commandLine.execute("rm 2".split(" ")));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> parkedTodoList = todoRepository.findAll(new IsRemoved());
        assertEquals(1, parkedTodoList.size());
        assertTrue(parkedTodoList.get(0).getText().startsWith(String.format("r %s 2019-02-07", SIMPLE_DATE_FORMAT.format(new Date()))));
    }

    @Test(timeout = 50)
    public void testTwoIdx() {
        assertSuccess(commandLine.execute("rm 2,4".split(" ")));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> removedTodoList = todoRepository.findAll(new IsRemoved());
        assertEquals(2, removedTodoList.size());
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
