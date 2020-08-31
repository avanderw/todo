package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

public class IsParked extends AbstractSpecification<Integer, Todo> {
    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return todo.getText().startsWith("p ");
    }

}
