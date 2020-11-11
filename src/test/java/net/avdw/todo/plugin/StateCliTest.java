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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static net.avdw.todo.TodoCliTestBootstrapper.cleanup;
import static net.avdw.todo.TodoCliTestBootstrapper.setup;
import static net.avdw.todo.TodoCliTestBootstrapper.warmup;

public class StateCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/kanban/.todo/todo.txt");
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
        cliTester.execute("ls --state").success()
                .contains("BACKLOG")
                .contains("IN-PROGRESS")
                .contains("DONE");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testGroupBy() {
        cliTester.execute("ls --group-by state").success();
    }
}
