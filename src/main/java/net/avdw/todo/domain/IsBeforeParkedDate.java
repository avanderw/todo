package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

import java.util.Date;

public class IsBeforeParkedDate extends AbstractSpecification<Integer, Todo> {
    private final Date date;

    public IsBeforeParkedDate(final Date date) {
        this.date = new Date(date.getTime());
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        if (todo.isParked()) {
            return todo.getParkedDate().before(date);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("isBeforeParkedDate('%tF')", date);
    }
}
