package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

import java.util.Date;

public class IsAfterAddedDate extends AbstractSpecification<Integer, Todo> {
    private final Date date;

    public IsAfterAddedDate(final Date date) {
        this.date = date;
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return todo.getAdditionDate().after(date);
    }

    @Override
    public String toString() {
        return String.format("isAfterAddedDate('%tF')", date);
    }
}
