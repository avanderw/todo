package net.avdw.todo.extension.stats;

import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.mixin.BooleanFilterMixin;
import net.avdw.todo.core.mixin.CleanMixin;
import net.avdw.todo.core.mixin.DateFilterMixin;
import net.avdw.todo.core.mixin.RepositoryMixin;
import net.avdw.todo.core.style.TodoStyler;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.timing.TimingCalculator;
import net.avdw.todo.extension.timing.TimingStats;
import net.avdw.todo.extension.timing.TodoTiming;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Command(name = "stats", resourceBundle = "messages", description = "${bundle:stats}")
public class StatsCli implements Runnable {
    private final Date now = new Date();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final TemplatedResource templatedResource;
    private final TimingCalculator timingStatsCalculator;
    private final TodoTiming todoStatistic;
    private final TodoStyler todoStyler;
    @Mixin private BooleanFilterMixin booleanFilter;
    @Mixin private DateFilterMixin dateFilter;
    @Mixin private RepositoryMixin repositoryMixin;
    @Mixin private CleanMixin cleanMixin;
    @Spec private CommandSpec spec;

    @Inject
    public StatsCli(final TemplatedResource templatedResource, final TimingCalculator timingStatsCalculator, final TodoTiming todoStatistic, final TodoStyler todoStyler) {
        this.templatedResource = templatedResource;
        this.timingStatsCalculator = timingStatsCalculator;
        this.todoStatistic = todoStatistic;
        this.todoStyler = todoStyler;
    }

    private String days2period(final long totalDays) {
        long days = Math.abs(totalDays);
        final long years = Math.floorDiv(days, 365);
        days %= 365;
        final long months = Math.floorDiv(days, 30);
        days %= 30;
        final long weeks = Math.floorDiv(days, 7);
        days %= 7;

        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("%3s days (", totalDays));
        if (years != 0) {
            builder.append(String.format(" %sy", years));
        }
        if (months != 0) {
            builder.append(String.format(" %sm", months));
        }
        if (weeks != 0) {
            builder.append(String.format(" %sw", weeks));
        }
        builder.append(String.format(" %sd )", days));
        return builder.toString();
    }

    private void printCycleTimeStatistics(final List<Todo> todoList) {
        spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.STATS_CYCLE_TITLE));

        final TimingStats stats = timingStatsCalculator.calculateCycleTime(todoList);
        printStats(stats);

        final Optional<Todo> max = todoList.stream()
                .filter(t -> !todoStatistic.hasCycleTime(t))
                .filter(t -> !t.isDone())
                .filter(t -> !t.isRemoved())
                .filter(t -> !t.isParked())
                .filter(t -> !t.getExtValueList("started").isEmpty())
                .max(Comparator.comparing(t -> {
                    try {
                        return ChronoUnit.DAYS.between(simpleDateFormat.parse(t.getExtValueList("started").get(0)).toInstant(), now.toInstant());
                    } catch (final ParseException e) {
                        return 0L;
                    }
                }));
        if (max.isPresent()) {
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.STATS_MAX_CYCLE_TIME,
                    String.format("{time:'%s'}", days2period(ChronoUnit.DAYS.between(max.get().getAdditionDate().toInstant(), now.toInstant())))));
            printTodo(max.get());
        }

        final List<Todo> largeTimeTodoList = todoList.stream()
                .filter(t -> !todoStatistic.hasCycleTime(t))
                .filter(t -> !t.getExtValueList("started").isEmpty())
                .filter(t -> !t.isDone())
                .filter(t -> !t.isRemoved())
                .filter(t -> !t.isParked())
                .filter(t -> {
                    try {
                        return ChronoUnit.DAYS.between(simpleDateFormat.parse(t.getExtValueList("started").get(0)).toInstant(), now.toInstant()) > stats.getMean() + stats.getStdDev();
                    } catch (final ParseException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
        if (!largeTimeTodoList.isEmpty()) {
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.STATS_LARGE_CYCLE_TIME));
            largeTimeTodoList.forEach(this::printTodo);
        }

    }

    private void printLeadTimeStatistics(final List<Todo> todoList) {
        spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.STATS_LEAD_TITLE));

        final TimingStats stats = timingStatsCalculator.calculateLeadTime(todoList);
        printStats(stats);

        final Optional<Todo> max = todoList.stream()
                .filter(t -> !todoStatistic.hasLeadTime(t))
                .filter(t -> !t.isDone())
                .filter(t -> !t.isRemoved())
                .filter(t -> !t.isParked())
                .filter(t -> t.getAdditionDate() != null)
                .max(Comparator.comparing(t -> ChronoUnit.DAYS.between(t.getAdditionDate().toInstant(), now.toInstant())));
        if (max.isPresent()) {
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.STATS_MAX_LEAD_TIME,
                    String.format("{time:'%s'}", days2period(ChronoUnit.DAYS.between(max.get().getAdditionDate().toInstant(), now.toInstant())))));
            printTodo(max.get());
        }

        final List<Todo> largeTimeTodoList = todoList.stream()
                .filter(t -> !todoStatistic.hasLeadTime(t))
                .filter(t -> t.getAdditionDate() != null)
                .filter(t -> !t.isDone())
                .filter(t -> !t.isRemoved())
                .filter(t -> !t.isParked())
                .filter(t -> ChronoUnit.DAYS.between(t.getAdditionDate().toInstant(), now.toInstant()) > stats.getMean() + stats.getStdDev())
                .collect(Collectors.toList());
        if (!largeTimeTodoList.isEmpty()) {
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.STATS_LARGE_LEAD_TIME));
            largeTimeTodoList.forEach(this::printTodo);
        }
    }

    private void printReactionTimeStatistics(final List<Todo> todoList) {
        spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.STATS_REACTION_TITLE));

        final TimingStats stats = timingStatsCalculator.calculateReactionTime(todoList);
        printStats(stats);

        final Optional<Todo> max = todoList.stream()
                .filter(t -> !todoStatistic.hasReactionTime(t))
                .filter(t -> t.getAdditionDate() != null)
                .filter(t -> !t.isDone())
                .filter(t -> !t.isRemoved())
                .filter(t -> !t.isParked())
                .max(Comparator.comparing(t -> ChronoUnit.DAYS.between(t.getAdditionDate().toInstant(), now.toInstant())));
        if (max.isPresent()) {
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.STATS_MAX_REACTION_TIME,
                    String.format("{time:'%s'}", days2period(ChronoUnit.DAYS.between(max.get().getAdditionDate().toInstant(), now.toInstant())))));
            printTodo(max.get());
        }

        final List<Todo> largeTimeTodoList = todoList.stream()
                .filter(t -> !todoStatistic.hasReactionTime(t))
                .filter(t -> t.getAdditionDate() != null)
                .filter(t -> !t.isDone())
                .filter(t -> !t.isRemoved())
                .filter(t -> !t.isParked())
                .filter(t -> ChronoUnit.DAYS.between(t.getAdditionDate().toInstant(), now.toInstant()) > stats.getMean() + stats.getStdDev())
                .collect(Collectors.toList());
        if (!largeTimeTodoList.isEmpty()) {
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.STATS_LARGE_REACTION_TIME));
            largeTimeTodoList.forEach(this::printTodo);
        }
    }

    private void printStats(final TimingStats stats) {
        if (stats.getN() < 1) {
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.STATS_NOT_ENOUGH_DATA));
            return;
        }

        spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.CHART_BOX,
                String.format("{min:'%4s',max:'%4s',Q1:'%4s',Q3:'%4s',median:'%4s',trimmedMin:'%4s',trimmedMax:'%4s'}",
                        stats.getMin(),
                        stats.getMax(),
                        stats.getQ1(),
                        stats.getQ3(),
                        stats.getMedian(),
                        stats.getTrimmedMin(),
                        stats.getTrimmedMax()
                )));

        spec.commandLine().getOut().println("");

        spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.STATS_DESCRIPTIVE_TIME,
                String.format("{size:'%s',mean:'%s',stdDev:'%s',oneStdDev:'%s',twoStdDev:'%s',threeStdDev:'%s',minOneStdDev:'%s',zeroStdDev:'%s'}",
                        stats.getN(),
                        stats.getMean(),
                        stats.getStdDev(),
                        stats.getOneStdDev(),
                        stats.getTwoStdDev(),
                        stats.getThreeStdDev(),
                        stats.getMinOneStdDev(),
                        stats.getMean()
                )));
    }

    private void printTodo(final Todo todo) {
        spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                String.format("{idx:'%3s',todo:\"%s\"}", todo.getIdx(), todoStyler.style(todo).replaceAll("\"", "\\\\\""))));
    }

    @Override
    public void run() {
        Specification<Integer, Todo> specification = dateFilter.specification();
        specification = specification.and(booleanFilter.specification());

        Logger.trace(specification);
        final Repository<Integer, Todo> scopedRepository = repositoryMixin.repository();
        final List<Todo> todoList = scopedRepository.findAll(specification);

        if (todoList.isEmpty()) {
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.NO_TODO_FOUND));
        }
        todoList.forEach(Logger::trace);

        printReactionTimeStatistics(todoList);
        printCycleTimeStatistics(todoList);
        printLeadTimeStatistics(todoList);
    }
}
