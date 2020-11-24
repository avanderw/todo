package net.avdw.todo.plugin;

import lombok.SneakyThrows;
import net.avdw.todo.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.avdw.todo.TodoCliTestBootstrapper.*;

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
        cliTester = new CliTester(MainCli.class, new TestModule(todoPath));
        warmup(cliTester);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    @SneakyThrows
    public void testHelp() {
        cliTester.execute("browse --help").success();
    }
}