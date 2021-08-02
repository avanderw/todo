package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

import java.time.temporal.ChronoUnit;
import java.util.Date;

public class IsAfterParkedDate extends AbstractSpecification<Integer, Todo> {
    private final Date date;

    public IsAfterParkedDate(final Date date) {
        this.date = new Date(date.getTime());
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        if (todo.isParked()) {
            return todo.getParkedDate().toInstant().isAfter(date.toInstant().minus(1, ChronoUnit.DAYS));
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("isAfterParkedDate('%tF')", date);
    }
}
