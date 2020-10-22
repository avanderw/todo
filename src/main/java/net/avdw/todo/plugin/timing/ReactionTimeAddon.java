package net.avdw.todo.plugin.timing;

import com.google.inject.Inject;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.plugin.Addon;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.core.DayFormatter;
import net.avdw.todo.repository.Repository;

import java.util.List;

public class ReactionTimeAddon implements Addon {
    private final TimingCalculator statsCalculator;
    private final TimingConfidence statsConfidence;
    private final TimingMixin timingMixin;
    private final TemplatedResource templatedResource;
    private final TodoTiming todoTiming;

    @Inject
    public ReactionTimeAddon(final TimingCalculator statsCalculator, final TimingConfidence statsConfidence, final TemplatedResource templatedResource, final TimingMixin timingMixin, final TodoTiming todoTiming) {
        this.statsCalculator = statsCalculator;
        this.statsConfidence = statsConfidence;
        this.templatedResource = templatedResource;
        this.timingMixin = timingMixin;
        this.todoTiming = todoTiming;
    }

    @Override
    public String postList(final List<Todo> list, final Repository<Integer, Todo> repository) {
        String render = null;
        TimingStats statistic = statsCalculator.calculateReactionTime(list);
        if (statistic.getN() != 0) {
            render = templatedResource.populateKey(TimingKey.REACTION_SUMMARY,
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
            String reaction;
            if (todoTiming.hasReactionTime(todo)) {
                reaction = String.format("%s->", todoTiming.getReactionTime(todo));
            } else if (todoTiming.hasRunningReactionTime(todo)) {
                reaction = String.format("!%s->", todoTiming.getRunningReactionTime(todo));
            } else {
                reaction = " ";
            }
            return String.format("%6s", reaction);
        } else {
            return null;
        }
    }
}
