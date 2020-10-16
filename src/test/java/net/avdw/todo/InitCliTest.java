package net.avdw.todo;

import lombok.SneakyThrows;
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

    @Test(timeout = 256)
    @SneakyThrows
    public void testNotExists() {
        Files.deleteIfExists(todoPath);
        cliTester.execute("init").success();
    }

    @Test(timeout = 256)
    @SneakyThrows
    public void testExists() {
        Files.createDirectories(todoPath.getParent());
        Files.createFile(todoPath);
        cliTester.execute("init").success();
    }
}
