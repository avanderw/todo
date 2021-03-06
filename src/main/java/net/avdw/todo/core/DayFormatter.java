package net.avdw.todo.core;

public final class DayFormatter {
    private DayFormatter() {
    }

    public static String days2period(final double totalDays) {
        return days2period((long) totalDays);
    }

    public static String days2period(final long totalDays) {
        if (totalDays == 0) {
            return "          0d";
        }
        long days = Math.abs(totalDays);
        final long years = Math.floorDiv(days, 365);
        days %= 365;
        final long months = Math.floorDiv(days, 30);
        days %= 30;
        final long weeks = Math.floorDiv(days, 7);
        days %= 7;

        return ((years != 0) ? String.format("%sy", years) : "  ") +
                ((months != 0) ? String.format(" %2sm", months) : "    ") +
                ((weeks != 0) ? String.format(" %sw", weeks) : "   ") +
                ((days != 0) ? String.format(" %sd", days) : "   ");
    }
}
