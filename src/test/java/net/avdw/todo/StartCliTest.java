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
import java.text.SimpleDateFormat;

import static net.avdw.todo.TodoCliTestBootstrapper.cleanup;
import static net.avdw.todo.TodoCliTestBootstrapper.setup;
import static net.avdw.todo.TodoCliTestBootstrapper.warmup;

public class StartCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/start/.todo/todo.txt");
    private static CliTester cliTester;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

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

    @Test(timeout = 256)
    public void testBasic() {
        cliTester.execute("start 1,2").success().contains("started:").startsWith("[  1]");
    }

    @Test(timeout = 256)
    public void testHelp() {
        cliTester.execute("start --help").success().contains("Usage: todo start");
    }

    @Test(timeout = 256)
    public void testNoIdx() {
        cliTester.execute("start").failure();
    }
}
