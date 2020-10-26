package net.avdw.todo.style.parser;

import net.avdw.todo.Guard;

import java.util.Date;

public class DateGuard implements Guard<Date> {
    private final Date compareDate;
    private final MatchType matchType;

    DateGuard(final MatchType matchType, final Date compareDate) {
        this.matchType = matchType;
        this.compareDate = compareDate;
    }

    @Override
    public boolean isSatisfiedBy(final Date date) {
        return switch (matchType) {
            case EXACT -> date.equals(compareDate);
            case LT -> date.before(compareDate);
            case GT -> date.after(compareDate);
            default -> throw new UnsupportedOperationException();
        };
    }
}
