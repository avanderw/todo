package net.avdw.todo.style.painter;

import lombok.SneakyThrows;
import net.avdw.todo.Guard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatePainter implements IPainter {
    private final String color;
    private final Guard<Date> guard;
    private final String regex;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DatePainter(final Guard<Date> guard, final String regex, final String color) {
        this.guard = guard;
        this.regex = regex;
        this.color = color;
    }

    @SneakyThrows
    @Override
    public String paint(final String string, final String reset) {
        Matcher m = Pattern.compile(regex).matcher(string);
        int idx = 0;
        StringBuilder painted = new StringBuilder();
        while (m.find()) {
            String dateString = m.group("date");
            Date date = simpleDateFormat.parse(dateString);
            if (guard.isSatisfiedBy(date)) {
                painted.append(string, idx, m.start("date"));
                painted.append(color);
                painted.append(dateString);
                painted.append(reset);
            } else {
                painted.append(string, idx, m.start("date"));
                painted.append(dateString);
            }
            idx = m.end("date");
        }

        painted.append(string.substring(idx));
        return painted.toString();
    }
}
