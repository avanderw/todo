package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

import java.util.Date;

public class IsAfterDoneDate extends AbstractSpecification<Integer, Todo> {
    private final Date date;

    public IsAfterDoneDate(final Date date) {
        this.date = new Date(date.getTime());
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        if (todo.isDone()) {
            return todo.getDoneDate().after(date);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("isAfterDoneDate('%tF')", date);
    }
}
