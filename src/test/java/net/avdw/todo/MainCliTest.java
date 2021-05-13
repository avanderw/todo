package net.avdw.todo;

import lombok.SneakyThrows;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.avdw.todo.TodoCliTestBootstrapper.warmup;

public class MainCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/main/.todo/todo.txt");
    private static CliTester cliTester;

    @BeforeClass
    public static void beforeClass() {
        cliTester = new CliTester(todoPath);
        warmup(cliTester);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    @SneakyThrows
    public void testEmptyWithTodo() {
        Files.createDirectories(todoPath.getParent());
        Files.createFile(todoPath);
        cliTester.execute().success().contains("Usage");
        TodoCliTestBootstrapper.cleanup(todoPath);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testEmptyWithoutTodo() {
        cliTester.execute().success().contains("Usage").contains("todo init");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testUpdateFeature() {
        cliTester.execute("update --help").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testVersion() {
        cliTester.execute("--version").success();
    }
}