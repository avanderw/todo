package net.avdw.todo.core;

import lombok.SneakyThrows;
import net.avdw.todo.CliTester;
import net.avdw.todo.TestConstant;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static net.avdw.todo.TodoCliTestBootstrapper.cleanup;
import static net.avdw.todo.TodoCliTestBootstrapper.setup;
import static net.avdw.todo.TodoCliTestBootstrapper.warmup;

public class FilterByDateTest {
    private static final Path todoPath = Paths.get("target/test-resources/date/.todo/todo.txt");
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

    @Before
    @SneakyThrows
    public void beforeTest() {
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), todoPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    public void testExclusiveBefore() {
        cliTester.execute("ls --before added:2019-10-07").success().notContains("[ 41]");
        cliTester.execute("ls --before done:2020-10-21").success().notContains("[  2]");
        cliTester.execute("ls --before started:2019-09-09").success().notContains("[ 71]");
    }

    @Test
    public void testInclusiveAfter() {
        cliTester.execute("ls --after added:2019-10-07").success().contains("[ 41]");
        cliTester.execute("ls --after done:2020-10-21").success().contains("[  2]");
        cliTester.execute("ls --after started:2019-09-09").success().contains("[ 71]");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testSinceAlias() {
        cliTester.execute("ls --since added:2019-10-07").success().contains("[ 41]");
        cliTester.execute("ls --since done:2020-10-21").success().contains("[  2]");
        cliTester.execute("ls --since started:2019-09-09").success().contains("[ 71]");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testNow() {
        cliTester.execute("ls --before added:now").success();
    }


    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testBefore() {
        cliTester.execute("ls --before due:+60m").success().count("\\[", 4);
        cliTester.execute("ls --before added:+1d").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testAfter() {
        cliTester.execute("ls --after due:-50y").success().count("\\[", 4);
        cliTester.execute("ls --after added:-5w").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testAdded() {
        cliTester.execute("ls --after added:2015-01-01").success().contains("Showing 82 of 86");
        cliTester.execute("ls --before added:2035-01-01").success().contains("Showing 82 of 86");
    }


    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testDone() {
        cliTester.execute("ls --after done:2015-01-01").success().contains("Showing 3 of 86");
        cliTester.execute("ls --before done:2035-01-01").success().contains("Showing 3 of 86");
    }


    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testStarted() {
        cliTester.execute("ls --after started:2015-01-01").success().contains("Showing 27 of 86");
        cliTester.execute("ls --before started:2035-01-01").success().contains("Showing 27 of 86");
    }


    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testRemoved() {
        cliTester.execute("ls --after removed:2015-01-01").success().contains("Showing 2 of 86");
        cliTester.execute("ls --before removed:2035-01-01").success().contains("Showing 2 of 86");
    }


    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testParked() {
        cliTester.execute("ls --after parked:2015-01-01").success().contains("Showing 1 of 86");
        cliTester.execute("ls --before parked:2035-01-01").success().contains("Showing 1 of 86");
    }
}
