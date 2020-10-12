package net.avdw.todo.manage;

import net.avdw.todo.CliTester;
import net.avdw.todo.MainCli;
import net.avdw.todo.TestGuiceFactory;
import net.avdw.todo.TestModule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static net.avdw.todo.TodoCliTestBootstrapper.cleanup;
import static net.avdw.todo.TodoCliTestBootstrapper.warmup;

public class ManageCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/value/.todo/todo.txt");
    private static CliTester cliTester;

    @AfterClass
    public static void afterClass() {
        cleanup(todoPath);
    }

    @BeforeClass
    public static void beforeClass() {
        cliTester = new CliTester(MainCli.class, new TestGuiceFactory(new TestModule(todoPath)));
        warmup(cliTester);
    }

    @Test(timeout = 64)
    public void testBasic() {
        cliTester.execute("manage").success().contains("Usage");
    }

    @Test(timeout = 64)
    public void testHelp() {
        cliTester.execute("manage --help").success();
    }
}
