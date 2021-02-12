package net.avdw.todo.extension;

import net.avdw.todo.core.selector.Selector;
import net.avdw.todo.extension.moscow.MoscowTodoTxtExt;
import net.avdw.todo.extension.moscow.MoscowSelector;
import net.avdw.todo.extension.moscow.MoscowMapper;
import net.avdw.todo.extension.plan.PlanMapper;
import net.avdw.todo.extension.plan.PlanSelector;
import net.avdw.todo.extension.plan.PlanTodoTxtExt;

import java.util.HashSet;
import java.util.Set;

public class SelectorLoader {

    public Set<Selector> getSelectorSet() {
        Set<Selector> selectorSet = new HashSet<>();

        MoscowTodoTxtExt moscowTodoTxtExt = new MoscowTodoTxtExt();
        selectorSet.add(new MoscowSelector(new MoscowMapper(moscowTodoTxtExt), moscowTodoTxtExt));

        PlanTodoTxtExt planTodoTxtExt = new PlanTodoTxtExt();
        selectorSet.add(new PlanSelector(new PlanMapper(planTodoTxtExt), planTodoTxtExt));

        return selectorSet;
    }
}
