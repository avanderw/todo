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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static net.avdw.todo.TodoCliTestBootstrapper.cleanup;
import static net.avdw.todo.TodoCliTestBootstrapper.setup;
import static net.avdw.todo.TodoCliTestBootstrapper.warmup;

public class MoscowCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/moscow/.todo/todo.txt");
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
        cliTester.execute("moscow 1,2,3,13,15 --not moscow: --assign could").success()
                .contains("moscow:could").count("\\[", 3);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testFilter() {
        cliTester.execute("moscow --and refactor --not moscow: --assign could").success().contains("moscow:could");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testInteractive() {
        InputStream systemIn = System.in;
        ByteArrayInputStream testIn = new ByteArrayInputStream("could 2 2 2 n y should 2".getBytes());
        System.setIn(testIn);
        cliTester.execute("moscow 1,2,3,13,15").success().contains("moscow:could");
        System.setIn(systemIn);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testGroupBy() {
        cliTester.execute("ls --group-by moscow").success().contains("Must have");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testHelp() {
        cliTester.execute("moscow --help").success();
    }
}
