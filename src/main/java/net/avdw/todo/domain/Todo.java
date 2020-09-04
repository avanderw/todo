package net.avdw.todo.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.avdw.todo.Priority;
import net.avdw.todo.repository.IdType;
import org.tinylog.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EqualsAndHashCode
public class Todo implements IdType<Integer> {
    private static final Pattern ADDITION_DATE_PATTERN = Pattern.compile("^(\\d\\d\\d\\d-\\d\\d-\\d\\d)|^x .*[\\d-]+.* (\\d\\d\\d\\d-\\d\\d-\\d\\d)");
    private static final Pattern COMPLETION_DATE_PATTERN = Pattern.compile("^x (\\d\\d\\d\\d-\\d\\d-\\d\\d)");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    @Getter
    @Setter
    private Integer id;
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

    public Todo(final Integer id, final String text) {
        this.id = Objects.requireNonNull(id);
        this.text = Objects.requireNonNull(text);
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

    private boolean complete() {
        return text.startsWith("x ");
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

    public Integer getIdx() {
        return id + 1;
    }

    public Optional<String> getKey(final String key) {
        int keyIdx = text.indexOf(String.format("%s:", key));
        if (keyIdx == -1) {
            return Optional.empty();
        }

        int valueStart = keyIdx + key.length() + 1;
        int valueBoundIdx = text.indexOf(" ", valueStart);
        int valueEnd = valueBoundIdx == -1 ? text.length() : valueBoundIdx;
        return Optional.of(text.substring(valueStart, valueEnd));
    }

    private Priority priority() {
        if (text.matches("^\\([A-Z]\\).*")) {
            return Priority.valueOf(text.substring(text.indexOf("(") + 1, text.indexOf(")")));
        } else {
            return null;
        }
    }

    public String toString() {
        return text;
    }
}
