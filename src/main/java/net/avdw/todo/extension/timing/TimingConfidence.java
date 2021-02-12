package net.avdw.todo.extension.timing;

public class TimingConfidence {
    public double estimate(TimingStats statistic) {
        double estimate = statistic.getN() > 8 ? statistic.getOneStdDev() : statistic.getTrimmedMax();
        return cap(estimate, statistic.getTrimmedMax());
    }

    private double cap(double value, double max) {
        return Math.max(Math.min(value, max), 0);
    }
}
