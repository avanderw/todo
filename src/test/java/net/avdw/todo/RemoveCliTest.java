package net.avdw.todo;

import lombok.SneakyThrows;
import net.avdw.todo.domain.IsRemoved;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static net.avdw.todo.TodoCliTestBootstrapper.*;
import static org.junit.Assert.*;

public class RemoveCliTest {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Path todoPath = Paths.get("target/test-resources/remove/.todo/todo.txt");
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

    @Test(timeout = 50)
    public void testNoIdx() {
        cliTester.execute("rm").failure();
    }

    @Test(timeout = 50)
    public void testRepeatIdx() {
        cliTester.execute("rm 5,5").success();
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> removedTodoList = todoRepository.findAll(new IsRemoved());
        assertEquals(1, removedTodoList.size());
        assertFalse(removedTodoList.get(0).getText().startsWith(String.format("r %s r ", SIMPLE_DATE_FORMAT.format(new Date()))));
    }

    @Test(timeout = 50)
    public void testPriorityRemoval() {
        cliTester.execute("pri 7 A").success();
        cliTester.execute("rm 7").success();
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> removedTodoList = todoRepository.findAll(new IsRemoved());
        assertEquals(1, removedTodoList.size());
        assertFalse(removedTodoList.get(0).getText().contains("(A)"));
    }

    @Test(timeout = 50)
    public void testOneIdx() {
        cliTester.execute("rm 2").success();
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> parkedTodoList = todoRepository.findAll(new IsRemoved());
        assertEquals(1, parkedTodoList.size());
        assertTrue(parkedTodoList.get(0).getText().startsWith(String.format("r %s 2019-02-07", SIMPLE_DATE_FORMAT.format(new Date()))));
    }

    @Test(timeout = 50)
    public void testTwoIdx() {
        cliTester.execute("rm 2,4").success();
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> removedTodoList = todoRepository.findAll(new IsRemoved());
        assertEquals(2, removedTodoList.size());
    }
}
