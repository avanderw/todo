package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.SneakyThrows;
import net.avdw.todo.repository.Any;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
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

public class SortCliTest {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String todayDate = SIMPLE_DATE_FORMAT.format(new Date());
    private static final Path todoPath = Paths.get("target/test-resources/sort/.todo/todo.txt");
    private static CommandLine commandLine;
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

    @After
    @SneakyThrows
    public void afterTest() {
        Files.deleteIfExists(todoPath);
        Files.deleteIfExists(todoPath.getParent());
        Files.deleteIfExists(todoPath.getParent().getParent());
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

    private void resetOutput() {
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));
    }

    @Test(timeout = 100)
    @SneakyThrows
    public void testBasic() {
        assertSuccess(commandLine.execute("sort"));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new Any<>());
        assertTrue(doneTodoList.get(0).getText().startsWith("2018-07-29"));
        assertTrue(outWriter.toString().startsWith("[  1]"));
    }

    @Test(timeout = 50)
    @SneakyThrows
    public void testKeys() {
        assertSuccess(commandLine.execute("sort importance,urgency".split(" ")));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new Any<>());
        assertTrue(doneTodoList.get(0).getText().startsWith("2020-03-10"));
        assertTrue(outWriter.toString().startsWith("[  1]"));
    }

    @Test(timeout = 50)
    public void testKeysPriority() {
        assertSuccess(commandLine.execute("pri 5 A".split(" ")));
        resetOutput();
        assertSuccess(commandLine.execute("sort importance,urgency".split(" ")));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new Any<>());
        assertTrue(doneTodoList.get(0).getText().startsWith("(A)"));
        assertTrue(doneTodoList.get(1).getText().startsWith("2020-03-10"));
        assertTrue(outWriter.toString().startsWith("[  1]"));
    }

    @Test(timeout = 50)
    public void testPriority() {
        assertSuccess(commandLine.execute("pri 5 A".split(" ")));
        resetOutput();
        assertSuccess(commandLine.execute("sort"));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new Any<>());
        assertTrue(doneTodoList.get(0).getText().startsWith("(A)"));
        assertTrue(outWriter.toString().startsWith("[  1]"));
    }

    static class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(List.class).to(LinkedList.class);
            bind(Path.class).toInstance(todoPath);
            bind(ResourceBundle.class).toInstance(ResourceBundle.getBundle("messages", Locale.ENGLISH));
        }

        @Provides
        @Singleton
        Repository<Integer, Todo> todoRepository(final Path todoPath) {
            return new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        }
    }

}
