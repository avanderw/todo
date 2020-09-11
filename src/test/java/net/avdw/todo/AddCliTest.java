package net.avdw.todo;

import lombok.SneakyThrows;
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
        cliTester = new CliTester(MainCli.class, new TestGuiceFactory(new TestModule(todoPath)));
        warmup(cliTester);
    }

    @Before
    @SneakyThrows
    public void beforeTest() {
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), todoPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test(timeout = 100)
    public void testBasic() {
        cliTester.execute("add", "A new addition").success().startsWith("[ 73]");
    }

    @Test(timeout = 100)
    public void testPriority() {
        cliTester.execute("add -p", "A new addition").success().startsWith("[ 73] (A)");
    }

    @Test(timeout = 100)
    public void testDuplicate() {
        cliTester.execute("add", "A new addition").success();
        cliTester.execute("add", "A new addition").failure();
    }

    @Test(timeout = 50)
    public void testNoIdx() {
        cliTester.execute("add").failure();
    }
}
