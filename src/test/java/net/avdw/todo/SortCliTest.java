package net.avdw.todo;

import lombok.SneakyThrows;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static net.avdw.todo.TodoCliTestBootstrapper.*;
import static org.junit.Assert.assertTrue;

public class SortCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/sort/.todo/todo.txt");
    private static CliTester cliTester;

    @AfterClass
    public static void afterClass() {
        cleanup(todoPath);
    }

    @BeforeClass
    public static void beforeClass() {
        setup(todoPath);
        cliTester = new CliTester(RefactoredMainCli.class, new TestGuiceFactory(new net.avdw.todo.TestModule(todoPath)));
        warmup(cliTester);
    }

    @Before
    @SneakyThrows
    public void beforeTest() {
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), todoPath, StandardCopyOption.REPLACE_EXISTING);
    }


    @Test(timeout = 100)
    @SneakyThrows
    public void testBasic() {
        cliTester.execute("sort").success().startsWith("[  1]");
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new Any<>());
        assertTrue(doneTodoList.get(0).getText().startsWith("2018-07-29"));
    }

    @Test(timeout = 50)
    @SneakyThrows
    public void testKeys() {
        cliTester.execute("sort importance,urgency").success().startsWith("[  1]");
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new Any<>());
        assertTrue(doneTodoList.get(0).getText().startsWith("2020-03-10"));
    }

    @Test(timeout = 50)
    public void testKeysPriority() {
        cliTester.execute("pri 5 A");
        cliTester.execute("sort importance,urgency").startsWith("[  1]");
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new Any<>());
        assertTrue(doneTodoList.get(0).getText().startsWith("(A)"));
        assertTrue(doneTodoList.get(1).getText().startsWith("2020-03-10"));
    }

    @Test(timeout = 50)
    public void testPriority() {
        cliTester.execute("pri 5 A").success();
        cliTester.execute("sort").success().startsWith("[  1] (A)");
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new Any<>());
        assertTrue(doneTodoList.get(0).getText().startsWith("(A)"));
    }
}