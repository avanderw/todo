package net.avdw.todo.style;

import lombok.SneakyThrows;
import net.avdw.todo.color.ColorConverter;
import net.avdw.todo.style.painter.IPainter;
import org.fusesource.jansi.Ansi;
import org.tinylog.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateStyler implements IPainter {
    private static final Pattern ARGUMENT_REGEX = Pattern.compile("(-?\\d+)([dmy])([+-]?)");
    private static final ColorConverter COLOR_CONVERTER = new ColorConverter();
    private final String color;
    private final Date date;
    private final DefaultTextColor defaultTextColor;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final String tag;
    private boolean ascend;
    private boolean exact;

    public DateStyler(final String tag, final String argument, final String color, final DefaultTextColor defaultTextColor) {
        this.tag = tag;
        this.color = COLOR_CONVERTER.hexToAnsiFg(Integer.parseInt(color.replace("0x", ""), 16));
        this.defaultTextColor = defaultTextColor;

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
    public String paint(final String string, final String reset) {
        //String defaultColor = defaultTextColor.getFromText(string);
        Pattern pattern = switch (tag) {
            case "done" -> Pattern.compile("^[xpr] (\\d\\d\\d\\d-\\d\\d-\\d\\d)");
            case "add" -> Pattern.compile("^(\\d\\d\\d\\d-\\d\\d-\\d\\d)|^[xpr] [\\d-]+ (\\d\\d\\d\\d-\\d\\d-\\d\\d)|\\([A-Z]\\) (\\d\\d\\d\\d-\\d\\d-\\d\\d)");
            default -> Pattern.compile(String.format("(%s:(\\d\\d\\d\\d-\\d\\d-\\d\\d))", tag));
        };

        Matcher matcher = pattern.matcher(string);
        String replacedText = string;
        while (matcher.find()) {
            Date value;
            String replace = switch (tag) {
                case "add" -> {
                    String group = matcher.group(1);
                    group = matcher.group(2) != null ? matcher.group(2) : group;
                    group = matcher.group(3) != null ? matcher.group(3) : group;
                    yield group;
                }

                default -> matcher.group(1);
            };
            value = simpleDateFormat.parse(replace);

            if (exact) {
                if (value.equals(date)) {
                    Logger.trace("Styling group={}; {} == {}", replace, value, date);
                    replacedText = replacedText.replaceFirst(replace, Ansi.ansi().a(color).a(replace).a(reset).toString());
                }
            } else if (ascend) {
                if (value.after(date)) {
                    Logger.trace("Styling group={}; {} >= {}", replace, value, date);
                    replacedText = replacedText.replaceFirst(replace, Ansi.ansi().a(color).a(replace).a(reset).toString());
                }
            } else {
                if (value.before(date)) {
                    Logger.trace("Styling group={}; {} <= {}", replace, value, date);

                    replacedText = replacedText.replaceFirst(replace, Ansi.ansi().a(color).a(replace).a(reset).toString());
                }
            }
        }
        return replacedText;
    }
}
