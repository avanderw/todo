package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

import java.time.temporal.ChronoUnit;
import java.util.Date;

public class IsAfterAddedDate extends AbstractSpecification<Integer, Todo> {
    private final Date date;

    public IsAfterAddedDate(final Date date) {
        this.date = new Date(date.getTime());
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        if (todo.getAdditionDate() == null) {
            return false;
        }
        return todo.getAdditionDate().toInstant().isAfter(date.toInstant().minus(1, ChronoUnit.DAYS));
    }

    @Override
    public String toString() {
        return String.format("isAfterAddedDate('%tF')", date);
    }
}
