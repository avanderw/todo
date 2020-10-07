package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoStatistic;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import net.avdw.todo.style.StyleApplicator;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
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
    private BooleanFilter booleanFilter;
    @Mixin
    private DateFilter dateFilter;
    @Option(names = "--clean", descriptionKey = "list.clean.desc")
    private boolean isClean = false;
    @Mixin
    private RepositoryScope repositoryScope;
    @Spec
    private CommandSpec spec;
    @Inject
    private StyleApplicator styleApplicator;
    @Inject
    private TemplatedResourceBundle templatedResourceBundle;
    @Inject
    private TodoStatistic todoStatistic;
    @Inject
    private TodoTextCleaner todoTextCleaner;

    private String period(final long totalDays) {
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
        spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.STATS_CYCLE_TITLE));

        DescriptiveStatistics stats = new DescriptiveStatistics();
        todoList.stream()
                .filter(todoStatistic::hasCycleTime)
                .mapToLong(todoStatistic::getCycleTime)
                .forEach(stats::addValue);

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
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.STATS_MAX_CYCLE_TIME,
                    String.format("{time:'%s'}", period(ChronoUnit.DAYS.between(max.get().getAdditionDate().toInstant(), now.toInstant())))));
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
                        return ChronoUnit.DAYS.between(simpleDateFormat.parse(t.getTagValueList("started").get(0)).toInstant(), now.toInstant()) > stats.getMean() + stats.getStandardDeviation();
                    } catch (ParseException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
        if (!largeTimeTodoList.isEmpty()) {
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.STATS_LARGE_CYCLE_TIME));
            largeTimeTodoList.forEach(this::printTodo);
        }

    }

    private void printLeadTimeStatistics(final List<Todo> todoList) {
        spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.STATS_LEAD_TITLE));

        DescriptiveStatistics stats = new DescriptiveStatistics();
        todoList.stream()
                .filter(todoStatistic::hasLeadTime)
                .mapToLong(todoStatistic::getLeadTime)
                .forEach(stats::addValue);

        printStats(stats);

        Optional<Todo> max = todoList.stream()
                .filter(t -> !todoStatistic.hasLeadTime(t))
                .filter(t -> !t.isDone())
                .filter(t -> !t.isRemoved())
                .filter(t -> !t.isParked())
                .filter(t -> t.getAdditionDate() != null)
                .max(Comparator.comparing(t -> ChronoUnit.DAYS.between(t.getAdditionDate().toInstant(), now.toInstant())));
        if (max.isPresent()) {
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.STATS_MAX_LEAD_TIME,
                    String.format("{time:'%s'}", period(ChronoUnit.DAYS.between(max.get().getAdditionDate().toInstant(), now.toInstant())))));
            printTodo(max.get());
        }

        List<Todo> largeTimeTodoList = todoList.stream()
                .filter(t -> !todoStatistic.hasLeadTime(t))
                .filter(t -> t.getAdditionDate() != null)
                .filter(t -> !t.isDone())
                .filter(t -> !t.isRemoved())
                .filter(t -> !t.isParked())
                .filter(t -> ChronoUnit.DAYS.between(t.getAdditionDate().toInstant(), now.toInstant()) > stats.getMean() + stats.getStandardDeviation())
                .collect(Collectors.toList());
        if (!largeTimeTodoList.isEmpty()) {
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.STATS_LARGE_LEAD_TIME));
            largeTimeTodoList.forEach(this::printTodo);
        }
    }

    private void printReactionTimeStatistics(final List<Todo> todoList) {
        spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.STATS_REACTION_TITLE));

        DescriptiveStatistics stats = new DescriptiveStatistics();
        todoList.stream()
                .filter(todoStatistic::hasReactionTime)
                .mapToLong(todoStatistic::getReactionTime)
                .forEach(stats::addValue);

        printStats(stats);

        Optional<Todo> max = todoList.stream()
                .filter(t -> !todoStatistic.hasReactionTime(t))
                .filter(t -> t.getAdditionDate() != null)
                .filter(t -> !t.isDone())
                .filter(t -> !t.isRemoved())
                .filter(t -> !t.isParked())
                .max(Comparator.comparing(t -> ChronoUnit.DAYS.between(t.getAdditionDate().toInstant(), now.toInstant())));
        if (max.isPresent()) {
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.STATS_MAX_REACTION_TIME,
                    String.format("{time:'%s'}", period(ChronoUnit.DAYS.between(max.get().getAdditionDate().toInstant(), now.toInstant())))));
            printTodo(max.get());
        }

        List<Todo> largeTimeTodoList = todoList.stream()
                .filter(t -> !todoStatistic.hasReactionTime(t))
                .filter(t -> t.getAdditionDate() != null)
                .filter(t -> !t.isDone())
                .filter(t -> !t.isRemoved())
                .filter(t -> !t.isParked())
                .filter(t -> ChronoUnit.DAYS.between(t.getAdditionDate().toInstant(), now.toInstant()) > stats.getMean() + stats.getStandardDeviation())
                .collect(Collectors.toList());
        if (!largeTimeTodoList.isEmpty()) {
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.STATS_LARGE_REACTION_TIME));
            largeTimeTodoList.forEach(this::printTodo);
        }
    }

    private void printStats(final DescriptiveStatistics stats) {
        if (stats.getN() < 1) {
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.STATS_NOT_ENOUGH_DATA));
            return;
        }

        String min = String.format("%.0f", stats.getMin());
        String max = String.format("%.0f", stats.getMax());
        String q1 = String.format("%.0f", stats.getPercentile(25));
        String median = String.format("%.0f", stats.getPercentile(50));
        String q3 = String.format("%.0f", stats.getPercentile(75));
        String lowerIqr = String.format("%.0f", stats.getPercentile(25) - 1.5 * (stats.getPercentile(75) - stats.getPercentile(25)));
        String upperIqr = String.format("%.0f", stats.getPercentile(75) + 1.5 * (stats.getPercentile(75) - stats.getPercentile(25)));
        spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.CHART_BOX,
                String.format("{min:'%4s',max:'%4s',Q1:'%4s',Q3:'%4s',median:'%4s',trimmedMin:'%4s',trimmedMax:'%4s'}",
                        min,
                        max,
                        q1,
                        q3,
                        median,
                        lowerIqr,
                        upperIqr
                )));

        spec.commandLine().getOut().println("");
        long size = stats.getN();
        String mean = String.format("%.0f", stats.getMean());
        String stdDev = String.format("%.0f", stats.getStandardDeviation());
        String minOneStdDev = period((long) (stats.getMean() + -1 * stats.getStandardDeviation()));
        String zeroStdDev = period((long) (stats.getMean() + 0 * stats.getStandardDeviation()));
        String oneStdDev = period((long) (stats.getMean() + 1 * stats.getStandardDeviation()));
        String twoStdDev = period((long) (stats.getMean() + 2 * stats.getStandardDeviation()));
        String threeStdDev = period((long) (stats.getMean() + 3 * stats.getStandardDeviation()));
        spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.STATS_DESCRIPTIVE_TIME,
                String.format("{size:'%s',mean:'%s',stdDev:'%s',oneStdDev:'%s',twoStdDev:'%s',threeStdDev:'%s',minOneStdDev:'%s',zeroStdDev:'%s'}",
                        size,
                        mean,
                        stdDev,
                        oneStdDev,
                        twoStdDev,
                        threeStdDev,
                        minOneStdDev,
                        zeroStdDev
                )));
    }

    private void printTodo(final Todo todo) {
        String todoText = isClean ? todoTextCleaner.clean(todo) : todo.getText();
        spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.TODO_LINE_ITEM,
                String.format("{idx:'%3s',todo:\"%s\"}", todo.getIdx(), styleApplicator.apply(todoText).replaceAll("\"", "\\\\\""))));
    }

    @Override
    public void run() {
        Specification<Integer, Todo> specification = new Any<>();
        specification = dateFilter.specification(specification);
        specification = booleanFilter.specification(specification);
        Logger.trace(specification);

        Repository<Integer, Todo> scopedRepository = repositoryScope.allRepositories();
        List<Todo> todoList = scopedRepository.findAll(specification);

        if (todoList.isEmpty()) {
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.NO_TODO_FOUND));
        }
        todoList.forEach(Logger::trace);

        printReactionTimeStatistics(todoList);
        printCycleTimeStatistics(todoList);
        printLeadTimeStatistics(todoList);
    }
}
