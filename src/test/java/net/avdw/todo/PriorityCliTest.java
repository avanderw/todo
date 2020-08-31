package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.SneakyThrows;
import net.avdw.todo.domain.*;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

public class PriorityCliTest {
    private static final Path todoPath = Paths.get("target/test-resources/priority/.todo/todo.txt");
    private static CommandLine commandLine;
    private final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private StringWriter errWriter;
    private StringWriter outWriter;

    @Before
    @SneakyThrows
    public void beforeTest() {
        Files.createDirectories(todoPath.getParent());
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), todoPath, StandardCopyOption.REPLACE_EXISTING);
        commandLine = new CommandLine(RefactoredMainCli.class, new GuiceFactory(new TestModule()));
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));
    }

    @After
    @SneakyThrows
    public void afterTest() {
        Files.deleteIfExists(todoPath);
        Files.deleteIfExists(todoPath.getParent());
        Files.deleteIfExists(todoPath.getParent().getParent());
    }

    @Test(timeout = 50)
    public void testNoIdxEmptyPriorities() {
        assertSuccess(commandLine.execute("pri"));
    }

    @Test(timeout = 50)
    public void testNoIdxWithPriorities() {
        assertSuccess(commandLine.execute("pri 10 A".split(" ")));
        resetOutput();
        assertSuccess(commandLine.execute("pri"));
    }

    @Test(timeout = 50)
    public void testReassign() {
        assertSuccess(commandLine.execute("pri 10 F".split(" ")));
        resetOutput();
        assertSuccess(commandLine.execute("pri 10 D".split(" ")));
        assertFalse(outWriter.toString().contains("(D) (F)"));
        assertTrue(outWriter.toString().contains("(D) "));
    }

    @Test(timeout = 50)
    public void testAssign() {
        assertSuccess(commandLine.execute("pri 4 F".split(" ")));
        assertTrue(outWriter.toString().contains("[  4] (F) "));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
        assertEquals(1, priorityTodoList.size());
        priorityTodoList.forEach(todo -> assertTrue(priorityTodoList.get(0).getText().startsWith("(F) ")));
    }

    @Test(timeout = 50)
    public void testAssignMultiIdx() {
        assertSuccess(commandLine.execute("pri 1,2,3 F".split(" ")));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
        assertEquals(3, priorityTodoList.size());
        priorityTodoList.forEach(todo -> assertTrue(priorityTodoList.get(0).getText().startsWith("(F) ")));
    }

    @Test(timeout = 50)
    public void testAssume() {
        assertSuccess(commandLine.execute("pri 4 A".split(" ")));
        resetOutput();
        assertSuccess(commandLine.execute("pri 7".split(" ")));
        assertTrue(outWriter.toString().contains("(B) "));
    }

    @Test(timeout = 50)
    public void testAssumeMultiIdxOrder() {
        assertSuccess(commandLine.execute("pri 3,2,1".split(" ")));
        assertTrue(outWriter.toString().contains("[  3] (A) "));
        assertTrue(outWriter.toString().contains("[  2] (B) "));
        assertTrue(outWriter.toString().contains("[  1] (C) "));
    }

    @Test(timeout = 50)
    public void testRemove() {
        assertSuccess(commandLine.execute("pri 1,2,3 F".split(" ")));
        resetOutput();
        assertSuccess(commandLine.execute("pri 1,2 -r".split(" ")));
        assertFalse(outWriter.toString().contains("[  2] (F) "));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
        assertEquals(1, priorityTodoList.size());
    }

    @Test(timeout = 50)
    public void testCollapse() {
        assertSuccess(commandLine.execute("pri 1,10 A".split(" ")));
        assertSuccess(commandLine.execute("pri 2,8 C".split(" ")));
        assertSuccess(commandLine.execute("pri 5 E".split(" ")));
        resetOutput();
        assertSuccess(commandLine.execute("pri --collapse".split(" ")));
        assertTrue(outWriter.toString().contains("[  1] (A) "));
        assertTrue(outWriter.toString().contains("[  2] (B) "));
        assertTrue(outWriter.toString().contains("[  5] (C) "));
        assertFalse(outWriter.toString().contains("(E) "));
    }

    @Test(timeout = 50)
    public void testCollapseNoPriority() {
        assertSuccess(commandLine.execute("pri --collapse".split(" ")));
    }

    @Test(timeout = 50)
    public void testPrioritiseDone() {
        assertSuccess(commandLine.execute("do 1".split(" ")));
        resetOutput();
        assertSuccess(commandLine.execute("pri 1 A".split(" ")));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
        assertEquals(0, priorityTodoList.size());
    }

    @Test(timeout = 50)
    public void testPrioritiseRemoved() {
        assertSuccess(commandLine.execute("rm 1".split(" ")));
        resetOutput();
        assertSuccess(commandLine.execute("pri 1 A".split(" ")));
    }

    @Test(timeout = 50)
    public void testPrioritiseParked() {
        assertSuccess(commandLine.execute("park 1".split(" ")));
        resetOutput();
        assertSuccess(commandLine.execute("pri 1 A".split(" ")));
    }

    @Test(timeout = 50)
    public void testClear() {
        assertSuccess(commandLine.execute("pri 1,2,3,6 B".split(" ")));
        resetOutput();
        assertSuccess(commandLine.execute("pri --clear".split(" ")));
        assertFalse(outWriter.toString().contains("(B)"));
        Repository<Integer, Todo> todoRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
        assertEquals(0, priorityTodoList.size());
    }

    private void assertSuccess(final int exitCode) {
        if (!outWriter.toString().isEmpty()) {
            Logger.debug("Standard output:\n{}", outWriter.toString());
        }
        if (!errWriter.toString().isEmpty()) {
            Logger.error("Error output:\n{}", errWriter.toString());
        }
        assertEquals("MUST NOT HAVE error output", "", errWriter.toString());
        assertNotEquals("MUST HAVE standard output", "", outWriter.toString().trim());
        assertEquals(0, exitCode);
    }

    private void assertFailure(final int exitCode) {
        if (!outWriter.toString().isEmpty()) {
            Logger.debug("Standard output:\n{}", outWriter.toString());
        }
        if (!errWriter.toString().isEmpty()) {
            Logger.debug("Error output:\n{}", errWriter.toString());
        }
        assertNotEquals("MUST HAVE error output", "", errWriter.toString());
        assertEquals("MUST NOT HAVE standard output", "", outWriter.toString().trim());
        assertNotEquals(0, exitCode);
    }

    private void resetOutput() {
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));
    }

    static class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(List.class).to(LinkedList.class);
            bind(Set.class).to(HashSet.class);
            bind(Path.class).toInstance(todoPath);
            bind(ResourceBundle.class).toInstance(ResourceBundle.getBundle("messages", Locale.getDefault()));
        }

        @Provides
        @Singleton
        Repository<Integer, Todo> todoRepository(final Path todoPath) {
            return new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        }
    }
}
