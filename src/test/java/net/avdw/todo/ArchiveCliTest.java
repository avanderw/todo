package net.avdw.todo;

import lombok.SneakyThrows;
import net.avdw.todo.domain.*;
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
import static org.junit.Assert.assertEquals;

public class ArchiveCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/archive/.todo/todo.txt");
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

    @Test(timeout = 250)
    public void testBasic() {
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> doneParkedOrRemoved = todoRepository.findAll(new IsDone().or(new IsParked()).or(new IsRemoved()));
        assertEquals(6, doneParkedOrRemoved.size());

        cliTester.execute("archive").success();

        todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        doneParkedOrRemoved = todoRepository.findAll(new IsDone().or(new IsParked()).or(new IsRemoved()));
        assertEquals(0, doneParkedOrRemoved.size());
    }
}
