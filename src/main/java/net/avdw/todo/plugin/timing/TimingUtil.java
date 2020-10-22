package net.avdw.todo.plugin.timing;

public class TimingUtil {
    static String toRoundedJson(TimingStats statistic) {
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
