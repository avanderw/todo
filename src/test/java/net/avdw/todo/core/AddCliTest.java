package net.avdw.todo.core;

import lombok.SneakyThrows;
import net.avdw.todo.CliTester;
import net.avdw.todo.MainCli;
import net.avdw.todo.TestConstant;
import net.avdw.todo.TestModule;
import org.fusesource.jansi.Ansi;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;

import static net.avdw.todo.TodoCliTestBootstrapper.*;

public class AddCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/add/.todo/todo.txt");
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

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testBasic() {
        cliTester.execute("add", "A new addition").success().count("\\[", 1);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testPriority() {
        cliTester.execute("add -p", "A new addition").success().count("\\[", 1).contains ("(A)");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testDuplicate() {
        cliTester.execute("add", "A new addition").success();
        cliTester.execute("add", "A new addition").failure();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testNoIdx() {
        cliTester.execute("add").failure();
    }
}
