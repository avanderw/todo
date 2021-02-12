package net.avdw.todo.extension;

import lombok.SneakyThrows;
import net.avdw.todo.CliTester;
import net.avdw.todo.MainCli;
import net.avdw.todo.TestConstant;
import net.avdw.todo.TestModule;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;

import static net.avdw.todo.TodoCliTestBootstrapper.cleanup;
import static net.avdw.todo.TodoCliTestBootstrapper.setup;
import static net.avdw.todo.TodoCliTestBootstrapper.warmup;

public class LinkCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/link/.todo/todo.txt");
    private static CliTester cliTester;
    private final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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
    public void testAsync() {
        cliTester.execute("link 12 15").success();
        cliTester.execute("link 13 15").success().contains("link:1.2");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testBasic() {
        cliTester.execute("link 12 15").success()
                .contains("link:1").contains("link:1.1");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testDeep() {
        cliTester.execute("link 12,13 15").success();
        cliTester.execute("link 14 13").success().contains("link:1.2.1");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testHelp() {
        cliTester.execute("link --help").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testMulti() {
        cliTester.execute("link 12,13 15").success()
                .contains("link:1").contains("link:1.1").contains("link:1.2");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testReassign() {
        cliTester.execute("link 12,13 15").success().contains("link:1.1");
        cliTester.execute("link 12 16").success()
                .contains("link:2.1")
                .notContains("link:1.1");
    }
}
