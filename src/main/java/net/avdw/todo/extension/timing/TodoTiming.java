package net.avdw.todo.extension.timing;

import lombok.SneakyThrows;
import net.avdw.todo.domain.Todo;
import org.tinylog.Logger;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class TodoTiming {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final TimingExt timingExt;

    @Inject
    public TodoTiming(final TimingExt timingExt) {
        this.timingExt = timingExt;
    }

    @SneakyThrows
    public long getCycleTime(final Todo todo) {
        if (!hasCycleTime(todo)) {
            throw new UnsupportedOperationException();
        }

        final Date startedDate = timingExt.getValueList(todo).stream().max(Date::compareTo).orElseThrow();
        final long time = ChronoUnit.DAYS.between(startedDate.toInstant(), todo.getDoneDate().toInstant());
        if (time < 0) {
            Logger.debug("Cycle time is negative ({}), is this correct?\n" +
                    "  ( started = {} )\n" +
                    "  ( done    = {} )\n" +
                    "{}", time, simpleDateFormat.format(startedDate), simpleDateFormat.format(todo.getDoneDate()), todo);
        }
        return time;
    }

    public long getLeadTime(final Todo todo) {
        if (!hasLeadTime(todo)) {
            throw new UnsupportedOperationException();
        }

        final List<String> startedList = todo.getExtValueList("started");
        if (startedList.size() > 1) {
            Logger.debug("Multiple started tags found. Is this correct?\n" +
                    "  ( consider removing one of the started tags )\n" +
                    "{}", todo);
        }

        final long time = ChronoUnit.DAYS.between(todo.getAdditionDate().toInstant(), todo.getDoneDate().toInstant());
        if (time < 0) {
            Logger.debug("Lead time is negative ({}), is this correct?\n" +
                    "  ( added = {} )\n" +
                    "  ( done  = {} )\n" +
                    "{}", time, simpleDateFormat.format(todo.getAdditionDate()), simpleDateFormat.format(todo.getDoneDate()), todo);
        }
        return time;
    }

    @SneakyThrows
    public long getReactionTime(final Todo todo) {
        if (!hasReactionTime(todo)) {
            throw new UnsupportedOperationException();
        }

        final List<String> startedList = todo.getExtValueList("started");
        if (startedList.size() > 1) {
            Logger.debug("Multiple started tags found. Is this correct?\n{}", todo);
        }

        final long time = ChronoUnit.DAYS.between(todo.getAdditionDate().toInstant(), simpleDateFormat.parse(startedList.get(0)).toInstant());
        if (time < 0) {
            Logger.debug("Reaction time is negative ({}), is this correct?\n" +
                    "  ( added   = {} )\n" +
                    "  ( started = {} )\n" +
                    "{}", time, simpleDateFormat.format(todo.getAdditionDate()), startedList.get(0), todo);
        }
        return time;
    }

    public long getRunningCycleTime(final Todo todo) {
        if (!hasRunningCycleTime(todo)) {
            throw new UnsupportedOperationException();
        }

        final Date started = timingExt.getValueList(todo).stream().max(Date::compareTo).orElseThrow();
        final Date today = new Date();
        final long time = ChronoUnit.DAYS.between(started.toInstant(), today.toInstant());
        if (time < 0) {
            Logger.debug("Cycle time is negative ({}), is this correct?\n" +
                    "  ( started      = {} )\n" +
                    "  ( running-done = {} )\n" +
                    "{}", time, simpleDateFormat.format(started), simpleDateFormat.format(today), todo);
        }
        return time;
    }

    public long getRunningLeadTime(final Todo todo) {
        if (!hasRunningLeadTime(todo)) {
            throw new UnsupportedOperationException();
        }

        final long time = ChronoUnit.DAYS.between(todo.getAdditionDate().toInstant(), new Date().toInstant());
        if (time < 0) {
            Logger.debug("Lead time is negative ({}), is this correct?\n" +
                    "  ( added         = {} )\n" +
                    "  ( running-done  = {} )\n" +
                    "{}", time, simpleDateFormat.format(todo.getAdditionDate()), simpleDateFormat.format(todo.getDoneDate()), todo);
        }

        return time;
    }

    public long getRunningReactionTime(final Todo todo) {
        if (!hasRunningReactionTime(todo)) {
            throw new UnsupportedOperationException();
        }

        final Date today = new Date();
        final long time = ChronoUnit.DAYS.between(todo.getAdditionDate().toInstant(), today.toInstant());
        if (time < 0) {
            Logger.debug("Reaction time is negative ({}), is this correct?\n" +
                    "  ( added           = {} )\n" +
                    "  ( running-started = {} )\n" +
                    "{}", time, simpleDateFormat.format(todo.getAdditionDate()), simpleDateFormat.format(today), todo);
        }
        return time;
    }

    public boolean hasCycleTime(final Todo todo) {
        return !todo.getExtValueList("started").isEmpty() && todo.getDoneDate() != null;
    }

    public boolean hasLeadTime(final Todo todo) {
        return todo.getAdditionDate() != null && todo.getDoneDate() != null;
    }

    public boolean hasReactionTime(final Todo todo) {
        return todo.getAdditionDate() != null && !todo.getExtValueList("started").isEmpty();
    }

    public boolean hasRunningCycleTime(final Todo todo) {
        return !todo.isDone() && !todo.isParked() && !todo.isRemoved() && !todo.getExtValueList("started").isEmpty();
    }

    public boolean hasRunningLeadTime(final Todo todo) {
        return !todo.isDone() && !todo.isParked() && !todo.isRemoved() && todo.getAdditionDate() != null && !todo.getExtValueList("started").isEmpty();
    }

    public boolean hasRunningReactionTime(final Todo todo) {
        return !todo.isDone() && !todo.isParked() && !todo.isRemoved() && todo.getAdditionDate() != null;
    }
}
