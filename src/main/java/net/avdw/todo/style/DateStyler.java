package net.avdw.todo.style;

import lombok.SneakyThrows;
import net.avdw.todo.color.ColorConverter;
import org.fusesource.jansi.Ansi;
import org.tinylog.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateStyler implements IStyler {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Pattern ARGUMENT_REGEX = Pattern.compile("(-?\\d+)([dmy])([+-]?)");
    private static final ColorConverter COLOR_CONVERTER = new ColorConverter();
    private final String tag;
    private final String color;
    private final String defaultColor;
    private final Date date;
    private boolean ascend;
    private boolean exact;

    public DateStyler(final String tag, final String argument, final String color, final String defaultColor) {
        this.tag = tag;
        this.color = COLOR_CONVERTER.hexToAnsiFg(Integer.parseInt(color.replace("0x", ""), 16));
        this.defaultColor = defaultColor;

        Matcher matcher = ARGUMENT_REGEX.matcher(argument);
        if (matcher.find()) {
            int arg = Integer.parseInt(matcher.group(1));
            String modType = matcher.group(2);
            Calendar calendar = new GregorianCalendar();
            switch (modType) {
                case "d" -> calendar.add(Calendar.DAY_OF_MONTH, arg);
                case "m" -> calendar.add(Calendar.MONTH, arg);
                case "y" -> calendar.add(Calendar.YEAR, arg);
                default -> throw new UnsupportedOperationException(String.format("Unknown argument type (%s)", argument));
            }
            date = calendar.getTime();
            exact = matcher.group(3) == null;
            ascend = !exact && matcher.group(3).equals("+");
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    @SneakyThrows
    public String style(final String text) {
        Pattern pattern = switch (tag) {
            case "complete" -> Pattern.compile("^x (\\d\\d\\d\\d-\\d\\d-\\d\\d)");
            case "add" -> Pattern.compile("^(\\d\\d\\d\\d-\\d\\d-\\d\\d)|^x .*[\\d-]+.* (\\d\\d\\d\\d-\\d\\d-\\d\\d)");
            default -> Pattern.compile(String.format("(%s:(\\d\\d\\d\\d-\\d\\d-\\d\\d))", tag));
        };
        Matcher matcher = pattern.matcher(text);
        String replacedText = text;
        while (matcher.find()) {
            Date value;
            String replace;
            try {
                value = SIMPLE_DATE_FORMAT.parse(matcher.group(1));
                replace = matcher.group(1);
            } catch (Exception e) {
                value = SIMPLE_DATE_FORMAT.parse(matcher.group(2));
                replace = "add".equals(tag) ? matcher.group(2) : matcher.group(1);
            }
            if (exact) {
                if (value.equals(date)) {
                    Logger.trace("Styling group={}; {} == {}", replace, value, date);
                    replacedText = replacedText.replaceFirst(replace, Ansi.ansi().a(color).a(replace).a(defaultColor).toString());
                }
            } else if (ascend) {
                if (value.after(date)) {
                    Logger.trace("Styling group={}; {} >= {}", replace, value, date);
                    replacedText = replacedText.replaceFirst(replace, Ansi.ansi().a(color).a(replace).a(defaultColor).toString());
                }
            } else {
                if (value.before(date)) {
                    Logger.trace("Styling group={}; {} <= {}", replace, value, date);

                    replacedText = replacedText.replaceFirst(replace, Ansi.ansi().a(color).a(replace).a(defaultColor).toString());
                }
            }
        }
        return replacedText;
    }
}
