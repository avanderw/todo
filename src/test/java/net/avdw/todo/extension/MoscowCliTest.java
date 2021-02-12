package net.avdw.todo.extension;

import lombok.SneakyThrows;
import net.avdw.todo.CliTester;
import net.avdw.todo.MainCli;
import net.avdw.todo.TestConstant;
import net.avdw.todo.TestModule;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
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
import java.util.List;

import static net.avdw.todo.TodoCliTestBootstrapper.cleanup;
import static net.avdw.todo.TodoCliTestBootstrapper.setup;
import static net.avdw.todo.TodoCliTestBootstrapper.warmup;
import static org.junit.Assert.assertTrue;

public class MoscowCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/moscow/.todo/todo.txt");
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
    public void testBasic() {
        cliTester.execute("moscow 1,2,3,13,15 --not moscow: --assign could").success()
                .contains("moscow:could").count("\\[", 3);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testFilter() {
        cliTester.execute("moscow --and refactor --not moscow: --assign could").success().contains("moscow:could");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testInteractive() {
        InputStream systemIn = System.in;
        ByteArrayInputStream testIn = new ByteArrayInputStream("could 2 2 2 n y should 2".getBytes());
        System.setIn(testIn);
        cliTester.execute("moscow 1,2,3,13,15").success().contains("moscow:could");
        System.setIn(systemIn);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testGroupBy() {
        cliTester.execute("ls --group-by moscow").success().contains("Must have");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testHelp() {
        cliTester.execute("moscow --help").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testSort() {
        cliTester.execute("sort --by=moscow").success();
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneTodoList = todoRepository.findAll(new Any<>());
        assertTrue(doneTodoList.get(1).getText().startsWith("2020-03-13 Refactor"));
        assertTrue(doneTodoList.get(2).getText().startsWith("2020-03-13 Increase"));
    }
}
