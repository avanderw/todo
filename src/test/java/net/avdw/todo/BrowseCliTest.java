package net.avdw.todo;

import lombok.SneakyThrows;
import net.avdw.todo.CliTester;
import net.avdw.todo.MainCli;
import net.avdw.todo.TestGuiceFactory;
import net.avdw.todo.TestModule;
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

    @Test(timeout = 256)
    @SneakyThrows
    public void testHelp() {
        cliTester.execute("browse --help").success();
    }
}
