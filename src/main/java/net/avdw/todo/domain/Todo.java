package net.avdw.todo.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.avdw.todo.SuppressFBWarnings;
import net.avdw.todo.repository.IdType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EqualsAndHashCode
@SuppressFBWarnings(value = {"JLM_JSR166_UTILCONCURRENT_MONITORENTER", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"},
        justification = "Lombok generation makes these mute")
public class Todo implements IdType<Integer>, Comparable<Todo> {
    public static final Pattern ADDITION_DATE_PATTERN = Pattern.compile("^(\\d\\d\\d\\d-\\d\\d-\\d\\d)|^[xpr] [\\d-]+ (\\d\\d\\d\\d-\\d\\d-\\d\\d)|\\([A-Z]\\) (\\d\\d\\d\\d-\\d\\d-\\d\\d)");
    private static final Pattern COMPLETION_DATE_PATTERN = Pattern.compile("^x (\\d\\d\\d\\d-\\d\\d-\\d\\d)");
    private static final Pattern PARKED_DATE_PATTERN = Pattern.compile("^p (\\d\\d\\d\\d-\\d\\d-\\d\\d)");
    private static final Pattern REMOVED_DATE_PATTERN = Pattern.compile("^r (\\d\\d\\d\\d-\\d\\d-\\d\\d)");
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Getter
    private final String text;
    @Getter(lazy = true)
    private final List<String> contextList = contextList();
    @Getter(lazy = true)
    private final List<String> projectList = projectList();
    @Getter(lazy = true)
    private final boolean done = done();
    @Getter(lazy = true)
    private final boolean removed = removed();
    @Getter(lazy = true)
    private final boolean parked = parked();
    @Getter(lazy = true)
    private final Date additionDate = additionDate();
    @Getter(lazy = true)
    private final Date doneDate = doneDate();
    @Getter(lazy = true)
    private final Date parkedDate = parkedDate();
    @Getter(lazy = true)
    private final Date removedDate = removedDate();
    @Getter(lazy = true)
    private final Date lastChangeDate = lastChangeDate();
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
            String group = matcher.group(1);
            group = matcher.group(2) != null ? matcher.group(2) : group;
            group = matcher.group(3) != null ? matcher.group(3) : group;
            return simpleDateFormat.parse(group);
        } else {
            return null;
        }
    }

    @Override
    public int compareTo(final Todo todo) {
            return this.getText().compareTo(todo.getText());
    }

    private List<String> contextList() {
        List<String> contextList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\s@(\\S+)");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            contextList.add(matcher.group(1));
        }
        contextList.sort(Comparator.naturalOrder());
        return contextList;
    }

    private boolean done() {
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

    public List<String> getExtValueList(final String ext) {
        if (tagValueListMap.containsKey(ext)) {
            return tagValueListMap.get(ext);
        }

        List<String> tagValueList = new ArrayList<>();
        Pattern pattern = Pattern.compile(String.format("\\s%s:(\\S+)", ext));
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            tagValueList.add(matcher.group(1));
        }
        tagValueListMap.putIfAbsent(ext, tagValueList);
        return tagValueList;
    }

    private Date lastChangeDate() {
        Date changeDate = null;
        if (getAdditionDate() != null) {
            changeDate = getAdditionDate();
        }

        if (isDone()) {
            if (getAdditionDate() == null) {
                changeDate = getDoneDate();
            } else {
                changeDate = getDoneDate().after(changeDate) ? getDoneDate() : changeDate;
            }
        }

        if (isRemoved()) {
            if (getAdditionDate() == null) {
                changeDate = getRemovedDate();
            } else {
                changeDate = getRemovedDate().after(changeDate) ? getRemovedDate() : changeDate;
            }
        }

        if (isParked()) {
            if (getAdditionDate() == null) {
                changeDate = getParkedDate();
            } else {
                changeDate = getParkedDate().after(changeDate) ? getParkedDate() : changeDate;
            }
        }

        return changeDate;
    }

    private boolean parked() {
        return text.startsWith("p ");
    }

    @SneakyThrows
    private Date parkedDate() {
        Matcher matcher = PARKED_DATE_PATTERN.matcher(text);
        if (matcher.find()) {
            return simpleDateFormat.parse(matcher.group(1));
        } else {
            return null;
        }
    }

    private Priority priority() {
        if (text.matches("^\\([A-Z]\\).*")) {
            return Priority.valueOf(text.substring(text.indexOf("(") + 1, text.indexOf(")")));
        } else {
            return null;
        }
    }

    private List<String> projectList() {
        List<String> projectList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\s\\+(\\S+)");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            projectList.add(matcher.group(1));
        }
        projectList.sort(Comparator.naturalOrder());
        return projectList;
    }

    private boolean removed() {
        return text.startsWith("r ");
    }

    @SneakyThrows
    private Date removedDate() {
        Matcher matcher = REMOVED_DATE_PATTERN.matcher(text);
        if (matcher.find()) {
            return simpleDateFormat.parse(matcher.group(1));
        } else {
            return null;
        }
    }

    public String toString() {
        return text;
    }
}
