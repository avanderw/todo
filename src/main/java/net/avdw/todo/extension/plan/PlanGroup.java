package net.avdw.todo.extension.plan;

import net.avdw.todo.core.groupby.Group;
import net.avdw.todo.domain.Todo;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Locale;
import java.util.function.Function;

@Singleton
public class PlanGroup implements Group<Todo, String> {
    private final PlanTodoTxtExt planExt;
    private final PlanMapper planMapper;

    @Inject
    public PlanGroup(final PlanMapper planMapper, final PlanTodoTxtExt planExt) {
        this.planMapper = planMapper;
        this.planExt = planExt;
    }

    @Override
    public Function<Todo, String> collector() {
        return planMapper::map;
    }

    @Override
    public boolean isSatisfiedBy(final String selector) {
        return planExt.getSupportedExtList().contains(selector.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public String name() {
        return "plan";
    }
}
