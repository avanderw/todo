package net.avdw.todo.stats;

import com.google.inject.Inject;
import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.format.DayFormatter;
import picocli.CommandLine.Option;

import java.util.List;

public class StatisticMixin {
    @Option(names = "--detail", descriptionKey = "statistic.detail.desc")
    private boolean detail;
    @Inject
    private TemplatedResource templatedResource;
    @Inject
    private TimingConfidence timingConfidence;
    @Inject
    private TimingStatsCalculator timingStatsCalculator;

    public String renderStats(final List<Todo> list) {
        StringBuilder sb = new StringBuilder();
        Statistic reactionTimeStatistic = timingStatsCalculator.calculateReactionTime(list);
        if (reactionTimeStatistic.getN() != 0) {
            sb.append(templatedResource.populate(ResourceBundleKey.TIMING_REACTION,
                    String.format("{timing:'%s'}", DayFormatter.days2period(timingConfidence.estimate(reactionTimeStatistic)))));
            if (detail) {
                sb.append(templatedResource.populate(ResourceBundleKey.TIMING_DETAIL, toRoundedJson(reactionTimeStatistic)));
            }
            sb.append("\n");
        }

        Statistic cycleTimeStatistic = timingStatsCalculator.calculateCycleTime(list);
        if (cycleTimeStatistic.getN() != 0) {
            sb.append(templatedResource.populate(ResourceBundleKey.TIMING_CYCLE,
                    String.format("{timing:'%s'}", DayFormatter.days2period(timingConfidence.estimate(cycleTimeStatistic)))));
            if (detail) {
                sb.append(templatedResource.populate(ResourceBundleKey.TIMING_DETAIL, toRoundedJson(cycleTimeStatistic)));
            }
            sb.append("\n");
        }

        Statistic leadTimeStatistic = timingStatsCalculator.calculateLeadTime(list);
        if (leadTimeStatistic.getN() != 0) {
            sb.append(templatedResource.populate(ResourceBundleKey.TIMING_LEAD,
                    String.format("{timing:'%s'}", DayFormatter.days2period(timingConfidence.estimate(leadTimeStatistic)))));
            if (detail) {
                sb.append(templatedResource.populate(ResourceBundleKey.TIMING_DETAIL, toRoundedJson(leadTimeStatistic)));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private String toRoundedJson(Statistic statistic) {
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
