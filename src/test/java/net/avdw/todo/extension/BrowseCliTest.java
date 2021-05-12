package net.avdw.todo.extension;

import lombok.SneakyThrows;
import net.avdw.todo.CliTester;
import net.avdw.todo.TestConstant;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.avdw.todo.TodoCliTestBootstrapper.cleanup;
import static net.avdw.todo.TodoCliTestBootstrapper.setup;
import static net.avdw.todo.TodoCliTestBootstrapper.warmup;

public class BrowseCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/browse/.todo/todo.txt");
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
    public void testHelp() {
        cliTester.execute("browse --help").success();
    }
}
