package net.avdw.todo;

import net.avdw.todo.core.TodoEvaluator;
import net.avdw.todo.core.selector.ExtSelector;
import net.avdw.todo.core.selector.Selector;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.moscow.MoscowExt;
import net.avdw.todo.extension.moscow.MoscowSelector;
import net.avdw.todo.extension.moscow.MoscowMapper;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

public class TodoEvaluatorTest {
    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testEvaluator() {
        String input = "moscow + urgency: - size:";
        Todo todo = new Todo(-1, "Some todo with moscow:must urgency:5 size:3");
        MoscowExt moscowExt = new MoscowExt();
        Selector moscow = new MoscowSelector(new MoscowMapper(moscowExt), moscowExt);
        Selector urgency = new ExtSelector("urgency:");
        Selector size = new ExtSelector("size:");
        Set<Selector> selectorSet = new HashSet<>(Arrays.asList(moscow, urgency, size));
        TodoEvaluator todoEvaluator = new TodoEvaluator(input, selectorSet);
        assertEquals(15, todoEvaluator.evaluate(todo));
    }

}
