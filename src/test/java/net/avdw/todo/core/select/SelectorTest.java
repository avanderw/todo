package net.avdw.todo.core.select;

import net.avdw.todo.TestConstant;
import net.avdw.todo.core.selector.ContextSelector;
import net.avdw.todo.core.selector.ExtSelector;
import net.avdw.todo.core.selector.ProjectSelector;
import net.avdw.todo.core.selector.Selector;
import net.avdw.todo.core.selector.SpecificContextSelector;
import net.avdw.todo.core.selector.SpecificExtSelector;
import net.avdw.todo.core.selector.SpecificProjectSelector;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotEquals;

public class SelectorTest {
    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testContextParser() {
        String expression = "@moscow + context - size:";
        String validSelector = "context";
        String invalidSelector = "@moscow";
        Selector selector = new ContextSelector();
        assertTrue(validSelector, selector.isSatisfiedBy(validSelector));
        assertTrue(expression, selector.isSatisfiedBy(expression));
        assertFalse(invalidSelector, selector.isSatisfiedBy(invalidSelector));

        Repository<Integer, Todo> repository = new FileRepository<>(Paths.get("src/test/resources/.todo/todo.txt"), new TodoFileTypeBuilder());
        List<Todo> assignedList = repository.findAll(selector.specification());
        assertEquals(35, assignedList.size());
        assertNotEquals(repository.findAll(new Any<>()).size(), assignedList.size());
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testExtParser() {
        String expression = "+moscow + assigned: - size: - assigned:zubair";
        String validSelector = "assigned:";
        String invalidSelector = "assigned:zubair";
        Selector selector = new ExtSelector("assigned:");
        assertTrue(validSelector, selector.isSatisfiedBy(validSelector));
        assertFalse(invalidSelector, selector.isSatisfiedBy(invalidSelector));
        assertTrue(expression, selector.isSatisfiedBy(expression));

        Repository<Integer, Todo> repository = new FileRepository<>(Paths.get("src/test/resources/.todo/todo.txt"), new TodoFileTypeBuilder());
        List<Todo> assignedList = repository.findAll(selector.specification());
        assertEquals(82, assignedList.size());
        assertNotEquals(repository.findAll(new Any<>()).size(), assignedList.size());
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testProjectParser() {
        String expression = "+moscow + project - size:";
        String validSelector = "project";
        String invalidSelector = "+moscow";
        Selector selector = new ProjectSelector();
        assertTrue(validSelector, selector.isSatisfiedBy(validSelector));
        assertTrue(expression, selector.isSatisfiedBy(expression));
        assertFalse(invalidSelector, selector.isSatisfiedBy(invalidSelector));

        Repository<Integer, Todo> repository = new FileRepository<>(Paths.get("src/test/resources/.todo/todo.txt"), new TodoFileTypeBuilder());
        List<Todo> assignedList = repository.findAll(selector.specification());
        assertEquals(21, assignedList.size());
        assertNotEquals(repository.findAll(new Any<>()).size(), assignedList.size());
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testSpecificContextParser() {
        String expression = "@moscow + context - size:";
        String invalidSelector = "context";
        String validSelector = "@moscow";
        Selector selector = new SpecificContextSelector();
        assertTrue(validSelector, selector.isSatisfiedBy(validSelector));
        assertFalse(invalidSelector, selector.isSatisfiedBy(invalidSelector));
        assertTrue(expression, selector.isSatisfiedBy(expression));

        Repository<Integer, Todo> repository = new FileRepository<>(Paths.get("src/test/resources/.todo/todo.txt"), new TodoFileTypeBuilder());
        List<Todo> assignedList = repository.findAll(selector.specification());
        assertEquals(35, assignedList.size());
        assertNotEquals(repository.findAll(new Any<>()).size(), assignedList.size());
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testSpecificExtParser() {
        String expression = "+moscow + assigned:shane - size:";
        String invalidSelector = "assigned:";
        String validSelector = "assigned:shane";
        Selector selector = new SpecificExtSelector("assigned:shane");
        assertTrue(selector.isSatisfiedBy(validSelector));
        assertFalse(selector.isSatisfiedBy(invalidSelector));
        assertTrue(expression, selector.isSatisfiedBy(expression));

        Repository<Integer, Todo> repository = new FileRepository<>(Paths.get("src/test/resources/.todo/todo.txt"), new TodoFileTypeBuilder());
        List<Todo> assignedList = repository.findAll(selector.specification());
        assertEquals(10, assignedList.size());
        assertNotEquals(repository.findAll(new Any<>()).size(), assignedList.size());
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testSpecificProjectParser() {
        String expression = "+moscow + context - size:";
        String invalidSelector = "project";
        String validSelector = "+moscow";
        Selector selector = new SpecificProjectSelector();
        assertTrue(validSelector, selector.isSatisfiedBy(validSelector));
        assertTrue(expression, selector.isSatisfiedBy(expression));
        assertFalse(invalidSelector, selector.isSatisfiedBy(invalidSelector));

        Repository<Integer, Todo> repository = new FileRepository<>(Paths.get("src/test/resources/.todo/todo.txt"), new TodoFileTypeBuilder());
        List<Todo> assignedList = repository.findAll(selector.specification());
        assertEquals(21, assignedList.size());
        assertNotEquals(repository.findAll(new Any<>()).size(), assignedList.size());
    }

}
