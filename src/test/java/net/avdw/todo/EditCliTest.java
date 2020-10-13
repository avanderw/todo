package net.avdw.todo;

import lombok.SneakyThrows;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static net.avdw.todo.TodoCliTestBootstrapper.*;

public class EditCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/edit/.todo/todo.txt");
    private static CliTester cliTester;

    @AfterClass
    public static void afterClass() {
        cleanup(todoPath);
    }

    @BeforeClass
    public static void beforeClass() {
        setup(todoPath);
        cliTester = new CliTester(MainCli.class, new TestGuiceFactory(new TestModule(todoPath)));
        warmup(cliTester);
    }

    @Before
    @SneakyThrows
    public void beforeTest() {
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), todoPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test(timeout = 256)
    public void testAdd() {
        cliTester.execute("edit 5 --add addition").success().contains("addition").startsWith("[  5]");
    }

    @Test(timeout = 256)
    public void testAddRemove() {
        cliTester.execute("edit 5 --add addition --remove analyst:cassim").success()
                .contains("addition")
                .notContains("analyst:cassim")
                .startsWith("[  5]");
    }

    @Test(timeout = 256)
    public void testIdxNoOpts() {
        cliTester.execute("edit 5").failure();
    }

    @Test(timeout = 256)
    public void testMultiAdd() {
        cliTester.execute("edit 5,6 --add addition").success().startsWith("[  5]").count("addition", 2);
    }

    @Test(timeout = 256)
    public void testNoIdx() {
        cliTester.execute("edit").failure().notContains("init");
    }

    @Test(timeout = 256)
    public void testRemove() {
        cliTester.execute("edit 5 --remove analyst:cassim").success().notContains("analyst:cassim").startsWith("[  5]");
    }
}
