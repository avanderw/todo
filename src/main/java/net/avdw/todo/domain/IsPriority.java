package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

public class IsPriority extends AbstractSpecification<Todo> {
    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return todo.getPriority() != null;
    }
}
