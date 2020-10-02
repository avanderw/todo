package net.avdw.todo.domain;

import lombok.SneakyThrows;
import org.tinylog.Logger;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TodoStatistic {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @SneakyThrows
    public long getCycleTime(final Todo todo) {
        if (!hasCycleTime(todo)) {
            throw new UnsupportedOperationException();
        }

        List<String> startedList = todo.getTagValueList("started");
        if (startedList.size() > 1) {
            Logger.debug("Multiple started tags found. Is this correct?\n{}", todo);
        }

        long time = ChronoUnit.DAYS.between(simpleDateFormat.parse(startedList.get(0)).toInstant(), todo.getDoneDate().toInstant());
        if (time < 0) {
            Logger.debug("Cycle time is negative ({}), is this correct?\n{}", time, todo);
        }
        return time;
    }

    public long getLeadTime(final Todo todo) {
        if (!hasLeadTime(todo)) {
            throw new UnsupportedOperationException();
        }

        List<String> startedList = todo.getTagValueList("started");
        if (startedList.size() > 1) {
            Logger.debug("Multiple started tags found. Is this correct?\n{}", todo);
        }

        long time = ChronoUnit.DAYS.between(todo.getAdditionDate().toInstant(), todo.getDoneDate().toInstant());
        if (time < 0) {
            Logger.debug("Lead time is negative ({}), is this correct?\n{}", time, todo);
        }
        return time;
    }

    @SneakyThrows
    public long getReactionTime(final Todo todo) {
        if (!hasReactionTime(todo)) {
            throw new UnsupportedOperationException();
        }

        List<String> startedList = todo.getTagValueList("started");
        if (startedList.size() > 1) {
            Logger.debug("Multiple started tags found. Is this correct?\n{}", todo);
        }

        long time = ChronoUnit.DAYS.between(todo.getAdditionDate().toInstant(), simpleDateFormat.parse(startedList.get(0)).toInstant());
        if (time < 0) {
            Logger.debug("Reaction time is negative ({}), is this correct?\n{}", time, todo);
        }
        return time;
    }

    public boolean hasCycleTime(final Todo todo) {
        return !todo.getTagValueList("started").isEmpty() && todo.getDoneDate() != null;
    }

    public boolean hasLeadTime(final Todo todo) {
        return todo.getAdditionDate() != null && todo.getDoneDate() != null;
    }

    public boolean hasReactionTime(final Todo todo) {
        return todo.getAdditionDate() != null && !todo.getTagValueList("started").isEmpty();
    }
}
