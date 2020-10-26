package net.avdw.todo.style.parser;

import lombok.SneakyThrows;
import net.avdw.todo.Guard;
import org.tinylog.Logger;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateKeyParser {
    private final Pattern fixedRegex = Pattern.compile("^.+\\.(?<date>\\d\\d\\d\\d-\\d\\d-\\d\\d)(?<sign>[+-])?$");
    private final Pattern relativeRegex = Pattern.compile("^.*\\.(?<relative>-30)(?<type>[dmy])(?<sign>[+-])?$");
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private MatchType getMatchTypeFromSign(final String sign) {
        return switch (sign) {
            case "-" -> MatchType.LT;
            case "+" -> MatchType.GT;
            case "null" -> MatchType.EXACT;
            default -> throw new UnsupportedOperationException();
        };
    }

    @SneakyThrows
    public Guard<Date> parse(final String key) {
        MatchType matchType = null;
        Date compareDate = null;

        Matcher relativeMatcher = relativeRegex.matcher(key);
        if (relativeMatcher.find()) {
            matchType = getMatchTypeFromSign(String.valueOf(relativeMatcher.group("sign")));
            int arg = Integer.parseInt(relativeMatcher.group("relative"));
            Calendar calendar = new GregorianCalendar();
            switch (relativeMatcher.group("type")) {
                case "d" -> calendar.add(Calendar.DAY_OF_MONTH, arg);
                case "m" -> calendar.add(Calendar.MONTH, arg);
                case "y" -> calendar.add(Calendar.YEAR, arg);
                default -> throw new UnsupportedOperationException();
            }
            compareDate = calendar.getTime();
        } else {
            Logger.trace("Not relative date: {}", key);
        }

        Matcher fixedMatcher = fixedRegex.matcher(key);
        if (fixedMatcher.find()) {
            matchType = getMatchTypeFromSign(String.valueOf(fixedMatcher.group("sign")));
            compareDate = simpleDateFormat.parse(fixedMatcher.group("date"));
        } else {
            Logger.trace("Not fixed date: {}", key);
        }

        if (compareDate == null || matchType == null) {
            return new AnyDateGuard();
        }

        return new DateGuard(matchType, compareDate);
    }
}
