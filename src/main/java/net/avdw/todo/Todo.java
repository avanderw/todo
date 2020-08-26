package net.avdw.todo;

import lombok.Getter;
import lombok.SneakyThrows;
import net.avdw.todo.priority.Priority;
import org.tinylog.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Todo {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Pattern COMPLETION_DATE_PATTERN = Pattern.compile("^x (\\d\\d\\d\\d-\\d\\d-\\d\\d)");
    private static final Pattern ADDITION_DATE_PATTERN = Pattern.compile("^(\\d\\d\\d\\d-\\d\\d-\\d\\d)|^x .*[\\d-]+.* (\\d\\d\\d\\d-\\d\\d-\\d\\d)");
    @Getter
    private final String text;
    @Getter(lazy = true)
    private final boolean complete = complete();
    @Getter(lazy = true)
    private final Date additionDate = additionDate();
    @Getter(lazy = true)
    private final Date completionDate = completionDate();
    @Getter(lazy = true)
    private final Priority priority = priority();

    public Todo(final String text) {
        this.text = Objects.requireNonNull(text);
    }

    private boolean complete() {
        return text.startsWith("x ");
    }

    private Priority priority() {
        throw new UnsupportedOperationException();
    }

    @SneakyThrows
    private Date completionDate() {
        Matcher matcher = COMPLETION_DATE_PATTERN.matcher(text);
        if (matcher.find()) {
            Logger.trace("Completion date pattern={}, group={}", COMPLETION_DATE_PATTERN.pattern(), matcher.group(1));
            return SIMPLE_DATE_FORMAT.parse(matcher.group(1));
        } else {
            return null;
        }
    }

    @SneakyThrows
    private Date additionDate() {
        Matcher matcher = ADDITION_DATE_PATTERN.matcher(text);
        if (matcher.find()) {
            String group = matcher.group(1) == null ? matcher.group(2) : matcher.group(1);
            Logger.trace("Addition date pattern={}, group={}", ADDITION_DATE_PATTERN.pattern(), group);
            return SIMPLE_DATE_FORMAT.parse(group);
        } else {
            return null;
        }
    }
}
