package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

import java.util.Date;

public class IsBeforeAddedDate extends AbstractSpecification<Integer, Todo> {
    private final Date date;

    public IsBeforeAddedDate(final Date date) {
        this.date = new Date(date.getTime());
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return todo.getAdditionDate().before(date);
    }

    @Override
    public String toString() {
        return String.format("isBeforeAddedDate('%tF')", date);
    }
}
