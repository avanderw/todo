package net.avdw.todo.core;

import lombok.SneakyThrows;
import net.avdw.todo.CliTester;
import net.avdw.todo.MainCli;
import net.avdw.todo.TestConstant;
import net.avdw.todo.TestModule;
import net.avdw.todo.domain.IsDone;
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

public class DoneCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/done/.todo/todo.txt");
    private static CliTester cliTester;
    private final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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
    public void testNoIdx() {
        cliTester.execute("do").failure();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testPriorityRemoval() {
        cliTester.execute("pri 7 A").success();
        cliTester.execute("do 7").success();
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new IsDone());
        assertEquals(4, doneTodoList.size());
        assertFalse(doneTodoList.get(0).getText().contains("(A)"));
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testRepeatIdx() {
        cliTester.execute("do 5,5").success();
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new IsDone());
        assertEquals(4, doneTodoList.size());
        assertFalse(doneTodoList.get(0).getText().startsWith(String.format("x %s x ", SIMPLE_DATE_FORMAT.format(new Date()))));
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testOneIdx() {
        cliTester.execute("do 2").success().startsWith("[  2] x ");
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new IsDone());
        assertEquals(3, doneTodoList.size());
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testTwoIdx() {
        cliTester.execute("do 2,4").success();
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new IsDone());
        assertEquals(4, doneTodoList.size());
    }
}
