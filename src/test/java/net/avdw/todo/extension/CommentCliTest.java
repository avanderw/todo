package net.avdw.todo.extension;

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

import static net.avdw.todo.TodoCliTestBootstrapper.*;

public class CommentCliTest {
    private static final String name = "comment";
    private static final Path todoPath = Paths.get(String.format("target/test-resources/%s/.todo/todo.txt", name));
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
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), CommentCliTest.todoPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testHelp() {
        cliTester.execute(name, "--help").success().notContains("Usage: todo [-hV] [COMMAND]");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testAdd() {
        cliTester.execute("comment 1 -m", "This is a comment").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testMultiAdd() {
        cliTester.execute("comment 1 -m", "This is a comment", "-m", "This is another comment").success()
                .contains("note:91f1e3 note:28c567");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testAlias() {
        cliTester.execute("note 1 -m", "This is a comment").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testExpansion() {
        cliTester.execute("note 1 --message", "This is a comment").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testNoteFound() {
        cliTester.execute("comment 2 -m", "This is a comment", "-m", "This is another comment").success()
                .contains("note:91f1e3 note:28c567");
        cliTester.execute("note 2").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testNoteNotFound() {
        cliTester.execute("note 3").failure().notContains("UnsupportedOperationException");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testEmpty() {
        cliTester.execute("note").failure().notContains("UnsupportedOperationException");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testTodoNotFound() {
        cliTester.execute("note 404").failure().notContains("UnsupportedOperationException");
    }
}
