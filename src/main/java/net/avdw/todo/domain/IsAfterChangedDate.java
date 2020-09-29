package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

import java.util.Date;

public class IsAfterChangedDate extends AbstractSpecification<Integer, Todo> {
    private final Date date;

    public IsAfterChangedDate(final Date date) {
        this.date = new Date(date.getTime());
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        if (todo.getLastChangeDate() == null) {
            return false;
        }
        return todo.getLastChangeDate().after(date);
    }

    @Override
    public String toString() {
        return String.format("isAfterChangedDate('%tF')", date);
    }
}
