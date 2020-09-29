package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

public class IsAdded extends AbstractSpecification<Integer, Todo> {
    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return todo.getAdditionDate() != null;
    }

    @Override
    public String toString() {
        return "isAdded";
    }
}
