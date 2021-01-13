package net.avdw.todo.core;

import net.avdw.todo.core.selector.ContextSelector;
import net.avdw.todo.core.selector.ExtSelector;
import net.avdw.todo.core.selector.ProjectSelector;
import net.avdw.todo.core.selector.Selector;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.plugin.moscow.MoscowExt;
import net.avdw.todo.plugin.moscow.MoscowMapper;
import net.avdw.todo.plugin.moscow.MoscowSelector;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TodoEvaluatorTest {

    @Test
    public void oneOfTwoExist() {
        Set<Selector> selectorSet = new HashSet<>();
        selectorSet.add(new ProjectSelector());
        selectorSet.add(new ContextSelector());
        selectorSet.add(new ExtSelector("urgency:"));

        MoscowExt moscowExt = new MoscowExt();
        MoscowMapper moscowMapper = new MoscowMapper(moscowExt);
        selectorSet.add(new MoscowSelector(moscowMapper, moscowExt));
        TodoEvaluator todoEvaluator = new TodoEvaluator("moscow + urgency:", selectorSet);
        assertEquals(21, todoEvaluator.evaluate(new Todo(-1, "2020-03-10 +Funeral_Cover compliance analyst:Julias importance:13 urgency:21")));
    }


    @Test
    public void twoExtExist() {
        Set<Selector> selectorSet = new HashSet<>();
        selectorSet.add(new ProjectSelector());
        selectorSet.add(new ContextSelector());
        selectorSet.add(new ExtSelector("urgency:"));
        selectorSet.add(new ExtSelector("importance:"));

        MoscowExt moscowExt = new MoscowExt();
        MoscowMapper moscowMapper = new MoscowMapper(moscowExt);
        selectorSet.add(new MoscowSelector(moscowMapper, moscowExt));
        TodoEvaluator todoEvaluator = new TodoEvaluator("importance: + urgency:", selectorSet);
        assertEquals(34, todoEvaluator.evaluate(new Todo(-1, "2020-03-10 +Funeral_Cover compliance analyst:Julias importance:13 urgency:21")));
    }

    @Test
    public void oneExtExist() {
        Set<Selector> selectorSet = new HashSet<>();
        selectorSet.add(new ProjectSelector());
        selectorSet.add(new ContextSelector());
        selectorSet.add(new ExtSelector("importance:"));

        MoscowExt moscowExt = new MoscowExt();
        MoscowMapper moscowMapper = new MoscowMapper(moscowExt);
        selectorSet.add(new MoscowSelector(moscowMapper, moscowExt));
        TodoEvaluator todoEvaluator = new TodoEvaluator("importance:", selectorSet);
        assertEquals(13, todoEvaluator.evaluate(new Todo(-1, "2020-03-10 +Funeral_Cover compliance analyst:Julias importance:13 urgency:21")));
    }

    @Test
    public void moscowExist() {
        Set<Selector> selectorSet = new HashSet<>();
        selectorSet.add(new ProjectSelector());
        selectorSet.add(new ContextSelector());

        MoscowExt moscowExt = new MoscowExt();
        MoscowMapper moscowMapper = new MoscowMapper(moscowExt);
        selectorSet.add(new MoscowSelector(moscowMapper, moscowExt));
        TodoEvaluator todoEvaluator = new TodoEvaluator("moscow", selectorSet);
        assertEquals(13, todoEvaluator.evaluate(new Todo(-1, "2020-03-10 +Funeral_Cover compliance analyst:Julias importance:13 urgency:21 moscow:must")));
    }
}