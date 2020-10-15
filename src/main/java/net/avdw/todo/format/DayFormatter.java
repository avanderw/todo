package net.avdw.todo.format;

public class DayFormatter {
    private DayFormatter() {
    }

    public static String days2period(final double totalDays) {
        return days2period((long) totalDays);
    }

    public static String days2period(final long totalDays) {
        long days = Math.abs(totalDays);
        long years = Math.floorDiv(days, 365);
        days %= 365;
        long months = Math.floorDiv(days, 30);
        days %= 30;
        long weeks = Math.floorDiv(days, 7);
        days %= 7;

        return String.format("%3s days (", totalDays) +
                ((years != 0) ? String.format(" %sy", years) : "   ") +
                ((months != 0) ? String.format(" %2sm", months) : "    ") +
                ((weeks != 0) ? String.format(" %sw", weeks) : "   ") +
                ((days != 0) ? String.format(" %sd )", days) : "    )");
    }
}
