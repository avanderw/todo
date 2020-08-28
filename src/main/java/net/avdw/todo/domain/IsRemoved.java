package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

public class IsRemoved extends AbstractSpecification<Todo> {
    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return todo.getText().startsWith("r ");
    }

}
