package net.avdw.todo.extension.plan;

import com.google.inject.Inject;
import net.avdw.todo.core.groupby.Group;
import net.avdw.todo.domain.Todo;

import java.util.Locale;
import java.util.function.Function;

public class PlanGroup implements Group<Todo, String> {
    private final PlanExt planExt;
    private final PlanMapper planMapper;

    @Inject
    public PlanGroup(final PlanMapper planMapper, final PlanExt planExt) {
        this.planMapper = planMapper;
        this.planExt = planExt;
    }

    @Override
    public Function<Todo, String> collector() {
        return (planMapper::map);
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
