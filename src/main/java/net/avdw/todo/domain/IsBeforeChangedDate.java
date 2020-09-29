package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

import java.util.Date;

public class IsBeforeChangedDate extends AbstractSpecification<Integer, Todo> {
    private final Date date;

    public IsBeforeChangedDate(final Date date) {
        this.date = new Date(date.getTime());
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        if (todo.getLastChangeDate() == null) {
            return false;
        }

        return todo.getLastChangeDate().before(date);
    }

    @Override
    public String toString() {
        return String.format("isBeforeChangedDate('%tF')", date);
    }
}
