package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.SneakyThrows;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.*;

public class MainCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/main/.todo/todo.txt");

    private CommandLine commandLine;
    private StringWriter errWriter;
    private StringWriter outWriter;

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
    public void beforeTest() {
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
    @SneakyThrows
    public void testEmptyWithTodo() {
        Files.createDirectories(todoPath.getParent());
        Files.createFile(todoPath);
        assertSuccess(commandLine.execute());
        assertTrue("SHOULD output usage help", outWriter.toString().contains("Usage"));
    }

    @Test(timeout = 50)
    public void testEmptyWithoutTodo() {
        assertSuccess(commandLine.execute());
        assertTrue("SHOULD output usage help", outWriter.toString().contains("Usage"));
    }

    @Test(timeout = 50)
    public void testVersion() {
        assertSuccess(commandLine.execute("--version"));
        assertNotEquals(2, outWriter.toString().length());
    }

    static class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(List.class).to(LinkedList.class);
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