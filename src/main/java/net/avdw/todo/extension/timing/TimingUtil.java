package net.avdw.todo.extension.timing;

public final class TimingUtil {
    private TimingUtil() {
    }

    static String toRoundedJson(final TimingStats statistic) {
        return String.format("{n:'%3d',mean:'%3.0f',stdDev:'%3.0f',trimmedMin:'%3.0f',q1:'%3.0f',q2:'%3.0f',q3:'%3.0f',trimmedMax:'%3.0f'}",
                statistic.getN(),
                statistic.getMean(),
                statistic.getStdDev(),
                statistic.getTrimmedMin(),
                statistic.getQ1(),
                statistic.getMedian(),
                statistic.getQ3(),
                statistic.getTrimmedMax());
    }
}
