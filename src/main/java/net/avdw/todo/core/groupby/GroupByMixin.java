package net.avdw.todo.core.groupby;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.plugin.change.ChangeTypeGroup;
import org.tinylog.Logger;
import picocli.CommandLine.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Singleton
public class GroupByMixin {
    private final List<Group<Todo, String>> groupByList = new ArrayList<>();
    @Inject private ChangeTypeGroup changeTypeGroup;
    @Inject private ContextGroup contextGroup;
    @Inject private MonthGroup monthGroup;
    @Option(names = "--group-by", descriptionKey = "list.group.by.desc", split = ",", paramLabel = "@|+|tag:|change")
    private List<String> groupBySelectorList = new ArrayList<>();
    @Inject private ProjectGroup projectGroup;

    private Collector<Todo, ?, Map<String, ?>> buildCollector(final List<Function<Todo, String>> groupByCollectorList) {
        Function f = groupByCollectorList.get(0);
        if (groupByCollectorList.size() > 1) {
            return Collectors.groupingBy(f, buildCollector(groupByCollectorList.subList(1, groupByCollectorList.size())));
        } else {
            return Collectors.groupingBy(f);
        }
    }

    public Collector<Todo, ?, Map<String, ?>> collector() {
        return buildCollector(groupByList.stream().map(Group::collector).collect(Collectors.toList()));
    }

    public int depth() {
        return groupByList.size();
    }

    public Group<Todo, String> getGroupByAtDepth(final int depth) {
        return groupByList.get(depth);
    }

    public boolean isEmpty() {
        return groupByList.size() == 0;
    }

    public void parse() {
        groupBySelectorList.forEach(selector -> {
            TagGroup tagGroup = new TagGroup(selector);
            if (tagGroup.isSatisfiedBy(selector)) {
                Logger.trace("group-by {} ({})", tagGroup, selector);
                groupByList.add(tagGroup);
            } else if (projectGroup.isSatisfiedBy(selector)) {
                Logger.trace("group-by {} ({})", projectGroup, selector);
                groupByList.add(projectGroup);
            } else if (contextGroup.isSatisfiedBy(selector)) {
                Logger.trace("group-by {} ({})", projectGroup, selector);
                groupByList.add(contextGroup);
            } else if (changeTypeGroup.isSatisfiedBy(selector)) {
                Logger.trace("group-by {} ({})", changeTypeGroup, selector);
                groupByList.add(changeTypeGroup);
            } else if (monthGroup.isSatisfiedBy(selector)) {
                Logger.trace("group-by {} ({})", monthGroup, selector);
                groupByList.add(monthGroup);
            } else {
                throw new UnsupportedOperationException();
            }
        });

    }
}
