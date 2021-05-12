package net.avdw.todo.extension.timing;

import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.DayFormatter;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.Mixin;
import net.avdw.todo.repository.Repository;

import javax.inject.Inject;
import java.util.List;

public class CycleTimeAddon implements Mixin {
    private final TimingCalculator statsCalculator;
    private final TimingConfidence statsConfidence;
    private final TimingMixin timingMixin;
    private final TemplatedResource templatedResource;
    private final TodoTiming todoTiming;

    @Inject
    public CycleTimeAddon(final TimingCalculator statsCalculator, final TimingConfidence statsConfidence, final TemplatedResource templatedResource, final TimingMixin timingMixin, final TodoTiming todoTiming) {
        this.statsCalculator = statsCalculator;
        this.statsConfidence = statsConfidence;
        this.templatedResource = templatedResource;
        this.timingMixin = timingMixin;
        this.todoTiming = todoTiming;
    }

    @Override
    public String postList(final List<Todo> list, final Repository<Integer, Todo> repository) {
        String render = null;
        final TimingStats statistic = statsCalculator.calculateCycleTime(list);
        if (statistic.getN() != 0) {
            render = templatedResource.populateKey(TimingKey.CYCLE_SUMMARY,
                    String.format("{fifty:'%s',eighty:'%s'}", DayFormatter.days2period(statistic.getMean()), DayFormatter.days2period(statsConfidence.estimate(statistic))));
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
            final String cycle;
            if (todoTiming.hasCycleTime(todo)) {
                cycle = String.format("%s->", todoTiming.getCycleTime(todo));
            } else if (todoTiming.hasRunningCycleTime(todo)) {
                cycle = String.format("!%s->", todoTiming.getRunningCycleTime(todo));
            } else {
                cycle = " ";
            }
            return String.format("%6s", cycle);
        } else {
            return null;
        }
    }
}
