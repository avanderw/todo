package net.avdw.todo.core;

import lombok.SneakyThrows;
import net.avdw.todo.CliTester;
import net.avdw.todo.TestConstant;
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

import static net.avdw.todo.TodoCliTestBootstrapper.cleanup;
import static net.avdw.todo.TodoCliTestBootstrapper.setup;
import static net.avdw.todo.TodoCliTestBootstrapper.warmup;
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
        cliTester = new CliTester(todoPath);
        warmup(cliTester);
    }

    @Before
    @SneakyThrows
    public void beforeTest() {
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), todoPath, StandardCopyOption.REPLACE_EXISTING);
    }


    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    @SneakyThrows
    public void testBasic() {
        cliTester.execute("sort").success().startsWith("[  1]");
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new Any<>());
        assertTrue(doneTodoList.get(0).getText().startsWith("(J) 2020-01-23"));
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    @SneakyThrows
    public void testKeys() {
        cliTester.execute("sort --by", "importance: + urgency:").success()
                .contains("[  2] 2019-02-07 Digital");
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new Any<>());
        assertTrue(doneTodoList.get(0).getText().startsWith("(J) 2020-01-23"));
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testKeysPriority() {
        cliTester.execute("pri 5 A");
        cliTester.execute("sort --by", "importance: + urgency:").success()
                .contains("[  3] 2019-02-07 Digital");
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new Any<>());
        assertTrue(doneTodoList.get(0).getText().startsWith("(A)"));
        assertTrue(doneTodoList.get(1).getText().startsWith("(J) 2020-01-23"));
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testPriority() {
        cliTester.execute("pri 5 A").success();
        cliTester.execute("sort").success().startsWith("[  1] (A)");
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new Any<>());
        assertTrue(doneTodoList.get(0).getText().startsWith("(A)"));
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testCustom() {
        cliTester.execute("sort --by", "importance: + urgency: - size:").success()
                .contains("[  2] 2019-02-07 Digital");
    }

    @Test//(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testPlugin() {
        cliTester.execute("sort --by", "moscow + urgency:").success()
                .contains("[  2] 2019-10-29 +ROB");
    }
}
