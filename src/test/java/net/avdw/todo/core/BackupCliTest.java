package net.avdw.todo.core;

import lombok.SneakyThrows;
import net.avdw.todo.CliTester;
import net.avdw.todo.TestConstant;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.avdw.todo.TodoCliTestBootstrapper.cleanup;
import static net.avdw.todo.TodoCliTestBootstrapper.setup;
import static net.avdw.todo.TodoCliTestBootstrapper.warmup;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BackupCliTest {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String todayDate = SIMPLE_DATE_FORMAT.format(new Date());
    private static final Path todoPath = Paths.get("target/test-resources/backup/.todo/todo.txt");
    private static CliTester cliTester;

    @AfterClass
    public static void afterClass() {
        cleanup(todoPath);
    }

    @BeforeClass
    public static void beforeClass() {
        setup(todoPath);
        cliTester = new CliTester(todoPath);
        warmup(cliTester);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    @SneakyThrows
    public void testBasic() {
        cliTester.execute("backup").success();

        assertTrue(Files.exists(todoPath.getParent().resolve(String.format("todo.txt_%s", todayDate))));
        assertFalse(Files.exists(todoPath.getParent().resolve(String.format("done.txt_%s", todayDate))));
        assertFalse(Files.exists(todoPath.getParent().resolve(String.format("parked.txt_%s", todayDate))));
        assertFalse(Files.exists(todoPath.getParent().resolve(String.format("removed.txt_%s", todayDate))));
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    @SneakyThrows
    public void testFull() {
        Files.copy(todoPath, todoPath.getParent().resolve("done.txt"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(todoPath, todoPath.getParent().resolve("parked.txt"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(todoPath, todoPath.getParent().resolve("removed.txt"), StandardCopyOption.REPLACE_EXISTING);

        cliTester.execute("backup").success();

        assertTrue(Files.exists(todoPath.getParent().resolve(String.format("todo.txt_%s", todayDate))));
        assertTrue(Files.exists(todoPath.getParent().resolve(String.format("done.txt_%s", todayDate))));
        assertTrue(Files.exists(todoPath.getParent().resolve(String.format("parked.txt_%s", todayDate))));
        assertTrue(Files.exists(todoPath.getParent().resolve(String.format("removed.txt_%s", todayDate))));
    }
}
