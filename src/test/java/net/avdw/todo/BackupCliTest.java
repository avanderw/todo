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

public class BackupCliTest {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String todayDate = SIMPLE_DATE_FORMAT.format(new Date());
    private static final Path todoPath = Paths.get("target/test-resources/backup/.todo/todo.txt");
    private static CommandLine commandLine;
    private StringWriter errWriter;
    private StringWriter outWriter;

    @After
    @SneakyThrows
    public void afterTest() {
        Files.deleteIfExists(todoPath);
        Files.deleteIfExists(todoPath.getParent().resolve("todo.txt_" + todayDate));
        Files.deleteIfExists(todoPath.getParent().resolve("done.txt"));
        Files.deleteIfExists(todoPath.getParent().resolve("done.txt_" + todayDate));
        Files.deleteIfExists(todoPath.getParent().resolve("parked.txt"));
        Files.deleteIfExists(todoPath.getParent().resolve("parked.txt_" + todayDate));
        Files.deleteIfExists(todoPath.getParent().resolve("removed.txt"));
        Files.deleteIfExists(todoPath.getParent().resolve("removed.txt_" + todayDate));
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
        Files.createDirectories(todoPath.getParent());
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), todoPath, StandardCopyOption.REPLACE_EXISTING);
        commandLine = new CommandLine(RefactoredMainCli.class, new GuiceFactory(new TestModule()));
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));
    }

    @Test(timeout = 50)
    @SneakyThrows
    public void testBasic() {
        assertSuccess(commandLine.execute("backup"));
        assertTrue(Files.exists(todoPath.getParent().resolve(String.format("todo.txt_%s", todayDate))));
        assertFalse(Files.exists(todoPath.getParent().resolve(String.format("done.txt_%s", todayDate))));
        assertFalse(Files.exists(todoPath.getParent().resolve(String.format("parked.txt_%s", todayDate))));
        assertFalse(Files.exists(todoPath.getParent().resolve(String.format("removed.txt_%s", todayDate))));
    }

    @Test(timeout = 150)
    @SneakyThrows
    public void testFull() {
        Files.copy(todoPath, todoPath.getParent().resolve("done.txt"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(todoPath, todoPath.getParent().resolve("parked.txt"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(todoPath, todoPath.getParent().resolve("removed.txt"), StandardCopyOption.REPLACE_EXISTING);
        assertSuccess(commandLine.execute("backup"));
        assertTrue(Files.exists(todoPath.getParent().resolve(String.format("todo.txt_%s", todayDate))));
        assertTrue(Files.exists(todoPath.getParent().resolve(String.format("done.txt_%s", todayDate))));
        assertTrue(Files.exists(todoPath.getParent().resolve(String.format("parked.txt_%s", todayDate))));
        assertTrue(Files.exists(todoPath.getParent().resolve(String.format("removed.txt_%s", todayDate))));
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
