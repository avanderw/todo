package net.avdw.todo.core.orderby;

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

public class OrderByTest {
    private static final Path todoPath = Paths.get("target/test-resources/order-by/.todo/todo.txt");
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
    public void testMixinMathSuccess() {
        cliTester.execute("ls --order-by", "importance: + urgency:").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testMixinMathNonsenseSuccess() {
        cliTester.execute("ls --order-by", "context + project").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testMixinFailure() {
        cliTester.execute("ls --order-by invalid-selector").failure();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testMixinSuccess() {
        cliTester.execute("ls --order-by context").success();
    }
}
