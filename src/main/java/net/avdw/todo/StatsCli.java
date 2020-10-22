package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.plugin.timing.TodoTiming;
import net.avdw.todo.filters.BooleanFilterMixin;
import net.avdw.todo.filters.DateFilterMixin;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import net.avdw.todo.plugin.timing.TimingStats;
import net.avdw.todo.plugin.timing.TimingCalculator;
import net.avdw.todo.style.StyleApplicator;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

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
    @Mixin
    private BooleanFilterMixin booleanFilter;
    @Mixin
    private DateFilterMixin dateFilter;
    @Option(names = "--clean", descriptionKey = "list.clean.desc")
    private boolean isClean = false;
    @Mixin
    private RepositoryMixin repositoryMixin;
    @Spec
    private CommandSpec spec;
    @Inject
    private StyleApplicator styleApplicator;
    @Inject
    private TemplatedResource templatedResource;
    @Inject
    private TimingCalculator timingStatsCalculator;
    @Inject
    private TodoTiming todoStatistic;
    @Inject
    private TodoTextCleaner todoTextCleaner;

    private String days2period(final long totalDays) {
        long days = Math.abs(totalDays);
        long years = Math.floorDiv(days, 365);
        days %= 365;
        long months = Math.floorDiv(days, 30);
        days %= 30;
        long weeks = Math.floorDiv(days, 7);
        days %= 7;

        StringBuilder builder = new StringBuilder();
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

        TimingStats stats = timingStatsCalculator.calculateCycleTime(todoList);
        printStats(stats);

        Optional<Todo> max = todoList.stream()
                .filter(t -> !todoStatistic.hasCycleTime(t))
                .filter(t -> !t.isDone())
                .filter(t -> !t.isRemoved())
                .filter(t -> !t.isParked())
                .filter(t -> !t.getTagValueList("started").isEmpty())
                .max(Comparator.comparing(t -> {
                    try {
                        return ChronoUnit.DAYS.between(simpleDateFormat.parse(t.getTagValueList("started").get(0)).toInstant(), now.toInstant());
                    } catch (ParseException e) {
                        return 0L;
                    }
                }));
        if (max.isPresent()) {
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.STATS_MAX_CYCLE_TIME,
                    String.format("{time:'%s'}", days2period(ChronoUnit.DAYS.between(max.get().getAdditionDate().toInstant(), now.toInstant())))));
            printTodo(max.get());
        }

        List<Todo> largeTimeTodoList = todoList.stream()
                .filter(t -> !todoStatistic.hasCycleTime(t))
                .filter(t -> !t.getTagValueList("started").isEmpty())
                .filter(t -> !t.isDone())
                .filter(t -> !t.isRemoved())
                .filter(t -> !t.isParked())
                .filter(t -> {
                    try {
                        return ChronoUnit.DAYS.between(simpleDateFormat.parse(t.getTagValueList("started").get(0)).toInstant(), now.toInstant()) > stats.getMean() + stats.getStdDev();
                    } catch (ParseException e) {
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

        TimingStats stats = timingStatsCalculator.calculateLeadTime(todoList);
        printStats(stats);

        Optional<Todo> max = todoList.stream()
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

        List<Todo> largeTimeTodoList = todoList.stream()
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

        TimingStats stats = timingStatsCalculator.calculateReactionTime(todoList);
        printStats(stats);

        Optional<Todo> max = todoList.stream()
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

        List<Todo> largeTimeTodoList = todoList.stream()
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
        String todoText = isClean ? todoTextCleaner.clean(todo) : todo.getText();
        spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                String.format("{idx:'%3s',todo:\"%s\"}", todo.getIdx(), styleApplicator.apply(todoText).replaceAll("\"", "\\\\\""))));
    }

    @Override
    public void run() {
        Specification<Integer, Todo> specification = dateFilter.specification();
        specification = specification.and(booleanFilter.specification());

        Logger.trace(specification);
        Repository<Integer, Todo> scopedRepository = repositoryMixin.repository();
        List<Todo> todoList = scopedRepository.findAll(specification);

        if (todoList.isEmpty()) {
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.NO_TODO_FOUND));
        }
        todoList.forEach(Logger::trace);

        printReactionTimeStatistics(todoList);
        printCycleTimeStatistics(todoList);
        printLeadTimeStatistics(todoList);
    }
}
