package net.avdw.todo.plugin.plan;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.domain.Todo;

@Singleton
public class PlanMapper {

    private final PlanExt planExt;

    @Inject
    PlanMapper(final PlanExt planExt) {
        this.planExt = planExt;
    }

    public String map(final Todo todo) {
        return planExt.getValue(todo).orElse("No");
    }
}
