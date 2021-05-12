package net.avdw.todo.extension.plan;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.AbstractSpecification;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HasPlan extends AbstractSpecification<Integer, Todo> {
    private final PlanTodoTxtExt planExt;

    @Inject
    HasPlan(final PlanTodoTxtExt planExt) {
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
