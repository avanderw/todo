package net.avdw.todo.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.avdw.todo.Priority;
import net.avdw.todo.SuppressFBWarnings;
import net.avdw.todo.repository.IdType;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EqualsAndHashCode
@SuppressFBWarnings(value = "JLM_JSR166_UTILCONCURRENT_MONITORENTER",
        justification = "Lombok notation does not bring in the confusion as it is hidden by generation")
public class Todo implements IdType<Integer> {
    private static final Pattern ADDITION_DATE_PATTERN = Pattern.compile("^(\\d\\d\\d\\d-\\d\\d-\\d\\d)|^x .*[\\d-]+.* (\\d\\d\\d\\d-\\d\\d-\\d\\d)");
    private static final Pattern COMPLETION_DATE_PATTERN = Pattern.compile("^x (\\d\\d\\d\\d-\\d\\d-\\d\\d)");
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Getter
    private final String text;
    @Getter(lazy = true)
    private final boolean complete = complete();
    @Getter(lazy = true)
    private final Date additionDate = additionDate();
    @Getter(lazy = true)
    private final Date doneDate = doneDate();
    @Getter(lazy = true)
    private final Priority priority = priority();
    @Getter
    @Setter
    private Integer id;
    private Map<String, List<String>> tagValueListMap = new HashMap<>();

    public Todo(final Integer id, final String text) {
        this.id = Objects.requireNonNull(id);
        this.text = Objects.requireNonNull(text);
    }

    @SneakyThrows
    private Date additionDate() {
        Matcher matcher = ADDITION_DATE_PATTERN.matcher(text);
        if (matcher.find()) {
            String group = matcher.group(1) == null ? matcher.group(2) : matcher.group(1);
            return simpleDateFormat.parse(group);
        } else {
            return null;
        }
    }

    private boolean complete() {
        return text.startsWith("x ");
    }

    @SneakyThrows
    private Date doneDate() {
        Matcher matcher = COMPLETION_DATE_PATTERN.matcher(text);
        if (matcher.find()) {
            return simpleDateFormat.parse(matcher.group(1));
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

    public List<String> getTagValueList(final String tag) {
        if (tagValueListMap.containsKey(tag)) {
            return tagValueListMap.get(tag);
        }

        List<String> tagValueList = new ArrayList<>();
        Pattern pattern = Pattern.compile(String.format("\\s%s:(\\S+)\\s?", tag));
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            tagValueList.add(matcher.group(1));
        }
        tagValueListMap.putIfAbsent(tag, tagValueList);
        return tagValueList;
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
