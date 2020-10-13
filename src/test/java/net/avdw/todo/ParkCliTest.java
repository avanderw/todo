package net.avdw.todo;

import lombok.SneakyThrows;
import net.avdw.todo.domain.IsParked;
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

public class ParkCliTest {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Path todoPath = Paths.get("target/test-resources/park/.todo/todo.txt");
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

    @Test(timeout = 256)
    public void testNoIdx() {
        cliTester.execute("park").failure();
    }

    @Test(timeout = 256)
    public void testRepeatIdx() {
        cliTester.execute("park 5,5").success();
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> parkedTodoList = todoRepository.findAll(new IsParked());
        assertEquals(2, parkedTodoList.size());
        assertFalse(parkedTodoList.get(0).getText().startsWith(String.format("p %s p ", SIMPLE_DATE_FORMAT.format(new Date()))));
    }

    @Test(timeout = 256)
    public void testPriorityRemoval() {
        cliTester.execute("pri 7 A").success();
        cliTester.execute("park 7").success();
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> parkedTodoList = todoRepository.findAll(new IsParked());
        assertEquals(2, parkedTodoList.size());
        assertFalse(parkedTodoList.get(0).getText().contains("(A)"));
    }


    @Test(timeout = 256)
    public void testOneIdx() {
        cliTester.execute("park 2").success();
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> parkedTodoList = todoRepository.findAll(new IsParked());
        assertEquals(2, parkedTodoList.size());
        assertTrue(parkedTodoList.get(0).getText().startsWith(String.format("p %s 2019-02-07", SIMPLE_DATE_FORMAT.format(new Date()))));
    }

    @Test(timeout = 256)
    public void testTwoIdx() {
        cliTester.execute("park 2,4").success();
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> parkedTodoList = todoRepository.findAll(new IsParked());
        assertEquals(3, parkedTodoList.size());
    }
}
