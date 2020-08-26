package net.avdw.todo;

import net.avdw.todo.repository.AbstractSpecification;

public class WithPriority extends AbstractSpecification<Todo> {
    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return todo.getPriority() != null;
    }
}
