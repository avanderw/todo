package net.avdw.todo.plugin;

import lombok.SneakyThrows;
import net.avdw.todo.CliTester;
import net.avdw.todo.MainCli;
import net.avdw.todo.TestConstant;
import net.avdw.todo.TestModule;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static net.avdw.todo.TodoCliTestBootstrapper.cleanup;
import static net.avdw.todo.TodoCliTestBootstrapper.setup;
import static net.avdw.todo.TodoCliTestBootstrapper.warmup;

public class PlanCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/plan/.todo/todo.txt");
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

    @Before
    @SneakyThrows
    public void beforeTest() {
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), todoPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testBasic() {
        cliTester.execute("plan 1,2,3,13,15 --not plan: --assign strategic").success()
                .contains("plan:strategic").count("\\[", 3);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testFilter() {
        cliTester.execute("plan --and refactor --not plan: --assign tactical").success().contains("plan:tactical");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testInteractive() {
        InputStream systemIn = System.in;
        ByteArrayInputStream testIn = new ByteArrayInputStream("operational 0 1 2 n y tactical 2".getBytes());
        System.setIn(testIn);
        cliTester.execute("plan 1,2,3,13,15").success().contains("plan:operational");
        System.setIn(systemIn);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testGroupBy() {
        cliTester.execute("ls --group-by plan").success().contains("Strategic plan");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testHelp() {
        cliTester.execute("plan --help").success();
    }
}
