package net.avdw.todo.extension.timing;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.List;

public class TimingCalculator {

    @Inject
    private TodoTiming todoTiming;

    public TimingStats calculateCycleTime(final List<Todo> todoList) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        todoList.stream()
                .filter(todoTiming::hasCycleTime)
                .mapToLong(todoTiming::getCycleTime)
                .forEach(stats::addValue);

        return map(stats);
    }

    public TimingStats calculateLeadTime(final List<Todo> todoList) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        todoList.stream()
                .filter(todoTiming::hasLeadTime)
                .mapToLong(todoTiming::getLeadTime)
                .forEach(stats::addValue);

        return map(stats);
    }

    public TimingStats calculateReactionTime(final List<Todo> todoList) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        todoList.stream()
                .filter(todoTiming::hasReactionTime)
                .mapToLong(todoTiming::getReactionTime)
                .forEach(stats::addValue);

        return map(stats);
    }

    private TimingStats map(final DescriptiveStatistics stats) {
        TimingStats statistic = new TimingStats();
        statistic.setIqr(stats.getPercentile(75) - stats.getPercentile(25));
        statistic.setMin(stats.getMin());
        statistic.setTrimmedMin(stats.getPercentile(25) - 1.5 * statistic.getIqr());
        statistic.setQ1(stats.getPercentile(25));
        statistic.setMedian(stats.getPercentile(50));
        statistic.setQ3(stats.getPercentile(75));
        statistic.setTrimmedMax(stats.getPercentile(75) + 1.5 * statistic.getIqr());
        statistic.setMax(stats.getMax());

        statistic.setN(stats.getN());
        statistic.setStdDev(stats.getStandardDeviation());
        statistic.setMean(stats.getMean());
        statistic.setMinOneStdDev(stats.getMean() + -1 * stats.getStandardDeviation());
        statistic.setOneStdDev(stats.getMean() + 1 * stats.getStandardDeviation());
        statistic.setTwoStdDev(stats.getMean() + 2 * stats.getStandardDeviation());
        statistic.setThreeStdDev(stats.getMean() + 3 * stats.getStandardDeviation());
        return statistic;
    }
}
