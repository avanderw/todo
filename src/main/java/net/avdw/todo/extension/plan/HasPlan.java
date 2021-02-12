package net.avdw.todo.extension.plan;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.AbstractSpecification;

@Singleton
public class HasPlan extends AbstractSpecification<Integer, Todo> {
    private final PlanExt planExt;

    @Inject
    HasPlan(final PlanExt planExt) {
        this.planExt = planExt;
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return planExt.isSatisfiedBy(todo);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
