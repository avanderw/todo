package net.avdw.todo.core;

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

import static net.avdw.todo.TodoCliTestBootstrapper.*;

public class GroupByDateTest {
    private static final Path todoPath = Paths.get("target/test-resources/group-by-date/.todo/todo.txt");
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
    public void testTag() {
        cliTester.execute("ls --group-by year:due").success();
        cliTester.execute("ls --group-by month:due").success();
        cliTester.execute("ls --group-by week:due").success();
        cliTester.execute("ls --group-by day:due").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testNesting() {
        cliTester.execute("ls --group-by year:due,started:").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testEmpty() {
        cliTester.execute("ls --group-by month").failure();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testAdded() {
        cliTester.execute("ls --group-by year:added").success();
        cliTester.execute("ls --group-by month:added").success();
        cliTester.execute("ls --group-by week:added").success();
        cliTester.execute("ls --group-by day:added").success();
    }


    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testDone() {
        cliTester.execute("ls --group-by year:done").success();
        cliTester.execute("ls --group-by month:done").success();
        cliTester.execute("ls --group-by week:done").success();
        cliTester.execute("ls --group-by day:done").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testNotFound() {
        cliTester.execute("ls --group-by year:not-found").success();
    }
}
