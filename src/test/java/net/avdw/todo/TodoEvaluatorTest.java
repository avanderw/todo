package net.avdw.todo;

import net.avdw.todo.core.TodoEvaluator;
import net.avdw.todo.core.selector.ExtSelector;
import net.avdw.todo.core.selector.Selector;
import net.avdw.todo.core.selector.SpecificExtSelector;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.plugin.moscow.MoscowExt;
import net.avdw.todo.plugin.moscow.MoscowExtSelector;
import net.avdw.todo.plugin.moscow.MoscowMapper;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

public class TodoEvaluatorTest {
    @Test(timeout = 256)
    public void testEvaluator() {
        String input = "moscow: + urgency: - size:";
        Todo todo = new Todo(-1, "Some todo with moscow:must urgency:5 size:3");
        ExtSelector moscow = new MoscowExtSelector(new MoscowMapper(new MoscowExt()));
        ExtSelector urgency = new ExtSelector("urgency:");
        ExtSelector size = new ExtSelector("size:");
        Set<Selector> selectorSet = new HashSet<>(Arrays.asList(moscow, urgency, size));
        TodoEvaluator todoEvaluator = new TodoEvaluator(input, selectorSet);
        assertEquals(15., todoEvaluator.evaluate(todo));
    }

}
