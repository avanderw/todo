package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

import java.util.Date;

public class IsBeforeRemovedDate extends AbstractSpecification<Integer, Todo> {
    private final Date date;

    public IsBeforeRemovedDate(final Date date) {
        this.date = new Date(date.getTime());
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        if (todo.isRemoved()) {
            return todo.getRemovedDate().before(date);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("isBeforeRemovedDate('%tF')", date);
    }
}
