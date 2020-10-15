package net.avdw.todo.groupby;

import net.avdw.todo.domain.Todo;
import org.tinylog.Logger;
import picocli.CommandLine.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class GroupByMixin {
    private final List<GroupBy<Todo, String, String>> groupByList = new ArrayList<>();
    @Option(names = "--group-by", descriptionKey = "list.group.by.desc", split = ",")
    private List<String> groupBySelectorList = new ArrayList<>();

    private Collector<Todo, ?, Map<String, ?>> buildCollector(final List<Function<Todo, String>> groupByCollectorList) {
        Function f = groupByCollectorList.get(0);
        if (groupByCollectorList.size() > 1) {
            return Collectors.groupingBy(f, buildCollector(groupByCollectorList.subList(1, groupByCollectorList.size())));
        } else {
            return Collectors.groupingBy(f);
        }
    }

    public Collector<Todo, ?, Map<String, ?>> collector() {
        return buildCollector(groupByList.stream().map(GroupBy::collector).collect(Collectors.toList()));
    }

    public int depth() {
        return groupByList.size();
    }

    public GroupBy<Todo, String, String> getGroupByAtDepth(final int depth) {
        return groupByList.get(depth);
    }

    public boolean isEmpty() {
        return groupByList.size() == 0;
    }

    public void setup() {
        if (!this.groupBySelectorList.isEmpty()) {
            this.groupBySelectorList.forEach(selector -> {
                GroupBy<Todo, String, String> projectGroupBy = new ProjectGroupBy();
                GroupBy<Todo, String, String> contextGroupBy = new ContextGroupBy();
                GroupBy<Todo, String, String> tagGroupBy = new TagGroupBy(selector.substring(0, selector.length() - 1));
                if (projectGroupBy.isSatisfiedBy(selector)) {
                    Logger.trace("group-by project ({})", selector);
                    groupByList.add(projectGroupBy);
                } else if (contextGroupBy.isSatisfiedBy(selector)) {
                    Logger.trace("group-by context ({})", selector);
                    groupByList.add(contextGroupBy);
                } else if (tagGroupBy.isSatisfiedBy(selector)) {
                    Logger.trace("group-by tag ({})", selector);
                    groupByList.add(tagGroupBy);
                } else {
                    throw new UnsupportedOperationException();
                }
            });
        }
    }
}
