package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.core.TodoListView;
import net.avdw.todo.core.mixin.CleanMixin;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.filters.BooleanFilterMixin;
import net.avdw.todo.filters.DateFilterMixin;
import net.avdw.todo.groupby.GroupByMixin;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import net.avdw.todo.stats.StatisticMixin;
import org.codehaus.plexus.util.StringUtils;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.util.List;
import java.util.Map;

@Command(name = "ls", resourceBundle = "messages", description = "${bundle:list}", mixinStandardHelpOptions = true)
public class ListCli implements Runnable {
    @Mixin private BooleanFilterMixin booleanFilterMixin;
    @Mixin private CleanMixin cleanMixin;
    @Mixin private DateFilterMixin dateFilterMixin;
    @Mixin private GroupByMixin groupByMixin;
    @Mixin private RepositoryMixin repositoryMixin;
    @Inject private RunningStats runningStats;
    @Spec private CommandSpec spec;
    @Mixin private StatisticMixin statisticMixin;
    @Inject private TemplatedResource templatedResource;
    @Inject private TodoListView todoListView;


    private void printList(final List<Todo> list, final Repository<Integer, Todo> repository) {
        spec.commandLine().getOut().println(todoListView.render(list, repository));
        spec.commandLine().getOut().print(statisticMixin.renderStats(list));
    }

    private void printMap(final Map<String, ?> map, final Repository<Integer, Todo> repository, final GroupByMixin groupByMixin, final int depth) {
        map.forEach((key, value) -> {
            String json = String.format("{type:'%s',title:'%s'}", groupByMixin.getGroupByAtDepth(depth).name(), StringUtils.capitalise(key.isBlank() ? "No" : key));
            String header = switch (depth) {
                case 0 -> templatedResource.populateKey(ResourceBundleKey.GROUP_BY_HEADING, json);
                case 1 -> templatedResource.populateKey(ResourceBundleKey.GROUP_BY_HEADING_2, json);
                case 2 -> templatedResource.populateKey(ResourceBundleKey.GROUP_BY_HEADING_3, json);
                default -> throw new UnsupportedOperationException();
            };
            spec.commandLine().getOut().println(header);

            if (value instanceof Map) {
                printMap((Map) value, repository, groupByMixin, depth + 1);
            } else if (value instanceof List) {
                printList((List) value, repository);
            } else {
                throw new UnsupportedOperationException();
            }
        });

    }

    @Override
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
            justification = "todoPath will always have a directory parent")
    public void run() {
        Specification<Integer, Todo> specification = dateFilterMixin.specification();
        specification = specification.and(booleanFilterMixin.specification());

        Logger.debug(specification);
        Repository<Integer, Todo> scopedRepository = repositoryMixin.repository();
        List<Todo> todoList = scopedRepository.findAll(specification);
        if (todoList.isEmpty()) {
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.NO_TODO_FOUND));
            return;
        }

        groupByMixin.setup();
        if (groupByMixin.depth() > 3) {
            spec.commandLine().getErr().println(templatedResource.populateKey(ResourceBundleKey.GROUP_BY_DEPTH_UNSUPPORTED));
            throw new UnsupportedOperationException();
        }

        if (groupByMixin.isEmpty()) {
            printList(todoList, scopedRepository);
        } else {
            Map<String, ?> groupTodoListMap = todoList.stream().collect(groupByMixin.collector());
            printMap(groupTodoListMap, scopedRepository, groupByMixin, 0);
        }
        spec.commandLine().getOut().println(runningStats.getDuration());
    }
}
