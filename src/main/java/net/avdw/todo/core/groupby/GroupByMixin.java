package net.avdw.todo.core.groupby;

import net.avdw.todo.domain.Todo;
import org.tinylog.Logger;
import picocli.CommandLine.Option;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Singleton
public class GroupByMixin {
    private final List<Group<Todo, String>> groupByList = new ArrayList<>();
    private final Set<Group<Todo, String>> groupBySet;
    @Option(names = "--group-by", descriptionKey = "list.group.by.desc", split = ",", paramLabel = "@|+|tag:|change")
    private List<String> groupBySelectorList = new ArrayList<>();

    @Inject
    GroupByMixin(final Set<Group<Todo, String>> groupBySet) {
        this.groupBySet = groupBySet;
    }

    private Collector<Todo, ?, Map<String, ?>> buildCollector(final List<Function<Todo, String>> groupByCollectorList) {
        final Function f = groupByCollectorList.get(0);
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
        groupByList.addAll(groupBySelectorList.stream().map(selector -> {
            final TagGroup tagGroup = new TagGroup(selector);
            if (tagGroup.isSatisfiedBy(selector)) {
                Logger.debug("Adding group-by mixin: {}", tagGroup);
                return tagGroup;
            } else {
                for (final Group<Todo, String> group : groupBySet) {
                    if (group.isSatisfiedBy(selector)) {
                        Logger.debug("Adding group-by mixin: {}", group);
                        return group;
                    }
                }
                throw new UnsupportedOperationException(String.format("group-by selector '%s' not supported", selector));
            }
        }).collect(Collectors.toList()));
    }
}
