package net.avdw.todo;

import lombok.SneakyThrows;
import net.avdw.todo.domain.IsPriority;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PriorityCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/priority/.todo/todo.txt");
    private static CliTester cliTester;

    @AfterClass
    public static void afterClass() {
        cleanup(todoPath);
    }

    @BeforeClass
    public static void beforeClass() {
        setup(todoPath);
        cliTester = new CliTester(MainCli.class, new TestGuiceFactory(new net.avdw.todo.TestModule(todoPath)));
        warmup(cliTester);
    }

    @Before
    @SneakyThrows
    public void beforeTest() {
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), todoPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test(timeout = 50)
    public void testAssign() {
        cliTester.execute("pri 4 F").success().contains("[  4] (F) ");
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
        assertEquals(2, priorityTodoList.size());
        priorityTodoList.forEach(todo -> assertTrue(priorityTodoList.get(0).getText().startsWith("(F) ")));
    }

    @Test(timeout = 50)
    public void testAssignMultiIdx() {
        cliTester.execute("pri 1,2,3 F").success();
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
        assertEquals(4, priorityTodoList.size());
        priorityTodoList.forEach(todo -> assertTrue(priorityTodoList.get(0).getText().startsWith("(F) ")));
    }

    @Test(timeout = 50)
    public void testAssume() {
        cliTester.execute("pri 4 A").success();
        cliTester.execute("pri 7").success().contains("(B) ");
    }

    @Test(timeout = 50)
    public void testAssumeMultiIdxOrder() {
        cliTester.execute("pri 3,2,1").success().contains("[  3] (A)").contains("[  2] (B)").contains("[  1] (C)");
    }

    @Test(timeout = 128)
    public void testCollapse() {
        cliTester.execute("pri 1,10 A").success();
        cliTester.execute("pri 2,8 C").success();
        cliTester.execute("pri 5 E").success();
        cliTester.execute("pri --collapse").success()
                .contains("[  1] (A) ").contains("[  2] (B) ").contains("[  5] (C) ")
                .notContains("(E)");
    }

    @Test(timeout = 50)
    public void testCollapseNoPriority() {
        cliTester.execute("pri --collapse").success();
    }

    @Test(timeout = 64)
    public void testHelp() {
        cliTester.execute("pri --help").success();
    }

    @Test(timeout = 50)
    public void testNoIdxEmptyPriorities() {
        cliTester.execute("pri").success();
    }

    @Test(timeout = 128)
    public void testNoIdxWithPriorities() {
        cliTester.execute("pri 10 A").success();
        cliTester.execute("pri").success().startsWith("[ 10] (A) ");
    }

    @Test(timeout = 128)
    public void testPrioritiseDone() {
        cliTester.execute("do 1").success();
        cliTester.execute("pri 1 A").success();
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
        assertEquals(1, priorityTodoList.size());
    }

    @Test(timeout = 128)
    public void testPrioritiseParked() {
        cliTester.execute("park 1").success();
        cliTester.execute("pri 1 A").success();
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
        assertEquals(1, priorityTodoList.size());
    }

    @Test(timeout = 50)
    public void testPrioritiseRemoved() {
        cliTester.execute("rm 1").success();
        cliTester.execute("pri 1 A").success();
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
        assertEquals(1, priorityTodoList.size());
    }

    @Test(timeout = 50)
    public void testReassign() {
        cliTester.execute("pri 10 F").success();
        cliTester.execute("pri 10 D").success().contains("(D) ").notContains("(D) (F)");
    }

    @Test(timeout = 50)
    public void testRemove() {
        cliTester.execute("pri 1,2,3 F").success();
        cliTester.execute("pri 1,2 -r").success().notContains("[  2] (F)");
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
        assertEquals(2, priorityTodoList.size());
    }

    @Test(timeout = 64)
    public void testRemoveAll() {
        cliTester.execute("pri 1,2,3 F").success();
        cliTester.execute("pri -R").success().notContains("(F)");
    }
}
