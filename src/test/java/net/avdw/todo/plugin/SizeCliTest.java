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

public class SizeCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/size/.todo/todo.txt");
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
        cliTester.execute("size 1,2,3,13,15 --not size: --assign 3").success()
                .contains("size:3").count("\\[", 5);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testFilter() {
        cliTester.execute("size --and refactor --not size: --assign 3").success().contains("size:3");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testInteractive() {
        InputStream systemIn = System.in;
        ByteArrayInputStream testIn = new ByteArrayInputStream("1 2 3 5 8 y 13".getBytes());
        System.setIn(testIn);
        cliTester.execute("size 1,2,3,13,15,16").success().contains("size:13");
        System.setIn(systemIn);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testGroupBy() {
        cliTester.execute("size --and refactor --not size: --assign 3").success().contains("size:3");
        cliTester.execute("ls --group-by size:").success().contains("3 Size");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testInteractiveEmpty() {
        cliTester.execute("size --and nothing,selected").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testHelp() {
        cliTester.execute("size --help").success();
    }
}
