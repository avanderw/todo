package net.avdw.todo.core;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class RelativeDate extends Date {
    public RelativeDate(final String relative) {
        boolean isAdd = relative.charAt(0) == '+';
        char type = relative.charAt(relative.length() - 1);
        long amount = Long.parseLong(relative.substring(1, relative.length() - 1));

        ChronoUnit chronoUnit;
        switch (type) {
            case 'y' -> chronoUnit = ChronoUnit.YEARS;
            case 'm' -> chronoUnit = ChronoUnit.MONTHS;
            case 'w' -> chronoUnit = ChronoUnit.WEEKS;
            case 'd' -> chronoUnit = ChronoUnit.DAYS;
            default -> throw new UnsupportedOperationException();
        }

        LocalDate date = LocalDate.now();
        if (isAdd) {
            date = date.plus(amount, chronoUnit);
        } else {
            date = date.minus(amount, chronoUnit);
        }

        setTime(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());
    }
}
