package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

public class IsContaining extends AbstractSpecification<Integer, Todo> {
    private final String text;

    public IsContaining(final String text) {
        this.text = text;
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return todo.getText().contains(text);
    }

}
