package net.avdw.todo.plugin.timing;

import com.google.inject.Inject;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.Addon;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.format.DayFormatter;
import net.avdw.todo.repository.Repository;

import java.util.List;

public class LeadTimeAddon implements Addon {
    private final TimingCalculator statsCalculator;
    private final TimingConfidence statsConfidence;
    private final TimingMixin timingMixin;
    private final TemplatedResource templatedResource;
    private final TodoTiming todoTiming;

    @Inject
    public LeadTimeAddon(final TimingCalculator statsCalculator, final TimingConfidence statsConfidence, final TemplatedResource templatedResource, final TimingMixin timingMixin, final TodoTiming todoTiming) {
        this.statsCalculator = statsCalculator;
        this.statsConfidence = statsConfidence;
        this.templatedResource = templatedResource;
        this.timingMixin = timingMixin;
        this.todoTiming = todoTiming;
    }

    @Override
    public String postList(final List<Todo> list, final Repository<Integer, Todo> repository) {
        String render = null;
        TimingStats statistic = statsCalculator.calculateLeadTime(list);
        if (statistic.getN() != 0) {
            render = templatedResource.populateKey(TimingKey.LEAD_SUMMARY,
                    String.format("{timing:'%s'}", DayFormatter.days2period(statsConfidence.estimate(statistic))));
            if (timingMixin.showDetail) {
                render += templatedResource.populateKey(TimingKey.SUMMARY_DETAIL, TimingUtil.toRoundedJson(statistic));
            }
        }

        return render;
    }

    @Override
    public String postTodo(final Todo todo) {
        return null;
    }

    @Override
    public String preList(final List<Todo> list, final Repository<Integer, Todo> repository) {
        return null;
    }

    @Override
    public String preTodo(final Todo todo) {
        if (timingMixin.showDetail) {
            String lead;
            if (todoTiming.hasLeadTime(todo)) {
                lead = String.format("%s", todoTiming.getLeadTime(todo));
            } else  if (todoTiming.hasRunningLeadTime(todo)) {
                lead = String.format("!%s", todoTiming.getRunningLeadTime(todo));
            } else {
                lead = " ";
            }
            return String.format("%4s", lead);
        } else {
            return null;
        }
    }
}
