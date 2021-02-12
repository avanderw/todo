package net.avdw.todo.extension.recur;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class RecurDuration {
    private final String extValue;

    public RecurDuration(final String extValue) {
        this.extValue = extValue;
    }

    private int getCalendarType() {
        return switch (extValue.toLowerCase(Locale.ENGLISH).charAt(extValue.length() - 1)) {
            case 'y' -> Calendar.YEAR;
            case 'm' -> Calendar.MONTH;
            case 'w' -> Calendar.WEEK_OF_MONTH;
            case 'd' -> Calendar.DAY_OF_MONTH;
            default -> throw new UnsupportedOperationException();
        };
    }

    public boolean isStrict() {
        return extValue.startsWith("+");
    }

    public Date recurFrom(final Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(getCalendarType(), getRecurValue());
        return calendar.getTime();
    }

    private int getRecurValue() {
        String value = extValue.toLowerCase(Locale.ENGLISH).replace("+", "");
        value = value.replaceAll("y", "");
        value = value.replaceAll("m", "");
        value = value.replaceAll("w", "");
        value = value.replaceAll("d", "");
        return Integer.parseInt(value);
    }
}
