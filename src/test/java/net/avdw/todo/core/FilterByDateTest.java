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
import java.text.SimpleDateFormat;

import static net.avdw.todo.TodoCliTestBootstrapper.cleanup;
import static net.avdw.todo.TodoCliTestBootstrapper.setup;
import static net.avdw.todo.TodoCliTestBootstrapper.warmup;

public class FilterByDateTest {
    private static final Path todoPath = Paths.get("target/test-resources/date/.todo/todo.txt");
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
    public void testExclusiveBefore() {
        cliTester.execute("ls --before added:2019-10-07").success().notContains("[ 41]");
        cliTester.execute("ls --before done:2020-10-21").success().notContains("[  2]");
        cliTester.execute("ls --before started:2019-09-09").success().notContains("[ 71]");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testInclusiveAfter() {
        cliTester.execute("ls --after added:2019-10-07").success().contains("[ 41]");
        cliTester.execute("ls --after done:2020-10-21").success().contains("[  2]");
        cliTester.execute("ls --after started:2019-09-09").success().contains("[ 71]");
    }

}
