package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

import java.util.Date;

public class IsBeforeDoneDate extends AbstractSpecification<Integer, Todo> {
    private final Date date;

    public IsBeforeDoneDate(final Date date) {
        this.date = new Date(date.getTime());
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        if (todo.isComplete()) {
            return todo.getDoneDate().before(date);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("isBeforeDoneDate('%tF')", date);
    }
}
