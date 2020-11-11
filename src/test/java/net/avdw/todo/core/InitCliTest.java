package net.avdw.todo.core;

import lombok.SneakyThrows;
import net.avdw.todo.CliTester;
import net.avdw.todo.MainCli;
import net.avdw.todo.TestConstant;
import net.avdw.todo.TestModule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.avdw.todo.TodoCliTestBootstrapper.cleanup;
import static net.avdw.todo.TodoCliTestBootstrapper.warmup;

public class InitCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/init/.todo/todo.txt");
    private static CliTester cliTester;

    @BeforeClass
    public static void beforeClass() {
        cliTester = new CliTester(MainCli.class, new TestModule(todoPath));
        warmup(cliTester);
    }

    @AfterClass
    public static void afterClass() {
        cleanup(todoPath);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    @SneakyThrows
    public void testNotExists() {
        Files.deleteIfExists(todoPath);
        cliTester.execute("init").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    @SneakyThrows
    public void testExists() {
        Files.createDirectories(todoPath.getParent());
        Files.createFile(todoPath);
        cliTester.execute("init").success();
    }
}
