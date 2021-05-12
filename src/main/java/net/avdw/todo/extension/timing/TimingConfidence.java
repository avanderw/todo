package net.avdw.todo.extension.timing;

import javax.inject.Inject;

public class TimingConfidence {
    @Inject
    TimingConfidence() {
    }

    public double estimate(final TimingStats statistic) {
        final double estimate = statistic.getN() > 8 ? statistic.getOneStdDev() : statistic.getTrimmedMax();
        return cap(estimate, statistic.getTrimmedMax());
    }

    private double cap(final double value, final double max) {
        return Math.max(Math.min(value, max), 0);
    }
}
