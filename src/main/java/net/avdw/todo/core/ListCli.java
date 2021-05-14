package net.avdw.todo.core;

import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.RunningStats;
import net.avdw.todo.SuppressFBWarnings;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.groupby.GroupByMixin;
import net.avdw.todo.core.groupby.ProjectGroup;
import net.avdw.todo.core.mixin.BooleanFilterMixin;
import net.avdw.todo.core.mixin.CleanMixin;
import net.avdw.todo.core.mixin.DateFilterMixin;
import net.avdw.todo.core.mixin.RepositoryMixin;
import net.avdw.todo.core.view.TodoListView;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.blocker.BlockerMixin;
import net.avdw.todo.extension.state.StateMixin;
import net.avdw.todo.extension.timing.TimingMixin;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Command(name = "ls", resourceBundle = "messages", description = "${bundle:list}", mixinStandardHelpOptions = true)
public class ListCli implements Runnable {
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");
    private final SimpleDateFormat monthSortFormat = new SimpleDateFormat("yyyy-MM");
    private final RunningStats runningStats;
    private final TemplatedResource templatedResource;
    private final TodoListView todoListView;
    @Option(names = "--projects", description = "List projects for the filters.")
    private boolean isProjectRender = false;
    @Option(names = "--top", description = "Limit the list to this value, 0=all.")
    private int top = 0;
    @Mixin private BlockerMixin blockerMixin;
    @Mixin private BooleanFilterMixin booleanFilterMixin;
    @Mixin private CleanMixin cleanMixin;
    @Mixin private DateFilterMixin dateFilterMixin;
    @Mixin private GroupByMixin groupByMixin;
    @Mixin private OrderByMixin orderByMixin;
    @Mixin private RepositoryMixin repositoryMixin;
    @Spec private CommandSpec spec;
    @Mixin private StateMixin stateMixin;
    @Mixin private TimingMixin timingMixin;

    @Inject
    ListCli(final RunningStats runningStats, final TemplatedResource templatedResource, final TodoListView todoListView) {
        this.runningStats = runningStats;
        this.templatedResource = templatedResource;
        this.todoListView = todoListView;
    }

    private void printList(final List<Todo> list, final Repository<Integer, Todo> repository) {
        if (isProjectRender) {
            final Map<String, List<Todo>> projectTodoMap = list.stream().collect(Collectors.groupingBy(new ProjectGroup().collector()));
            final String projects = projectTodoMap.keySet().stream()
                    .filter(key -> !key.isEmpty())
                    .sorted()
                    .collect(Collectors.joining(", "));
            if (projects.isEmpty()) {
                spec.commandLine().getOut().println("No projects");
            } else {
                spec.commandLine().getOut().println(projects);
            }
        } else {
            spec.commandLine().getOut().println(todoListView.render(list, repository, top));
        }
    }

    private void printMap(final Map<String, ?> map, final Repository<Integer, Todo> repository, final GroupByMixin groupByMixin, final int depth) {
        map.entrySet().stream()
                .sorted(Comparator.comparing(entry -> {
                    final String key = entry.getKey();
                    try {
                        return monthSortFormat.format(monthFormat.parse(key));
                    } catch (final ParseException e) {
                        return key;
                    }
                }))
                .forEach((entry) -> {
                    final String key = entry.getKey();
                    final Object value = entry.getValue();

                    final String capitalise = key.isBlank() ? "No" : Character.toUpperCase(key.charAt(0)) + key.substring(1);
                    final String json = String.format("{type:'%s',title:'%s'}", groupByMixin.getGroupByAtDepth(depth).name(), capitalise);
                    final String header = switch (depth) {
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
        specification = specification.and(blockerMixin.specification());

        Logger.debug(specification);
        final Repository<Integer, Todo> scopedRepository = repositoryMixin.repository();
        final List<Todo> todoList = scopedRepository.findAll(specification);

        orderByMixin.order(todoList);

        if (todoList.isEmpty()) {
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.NO_TODO_FOUND));
            return;
        }

        groupByMixin.parse();
        if (groupByMixin.depth() > 3) {
            spec.commandLine().getErr().println(templatedResource.populateKey(ResourceBundleKey.GROUP_BY_DEPTH_UNSUPPORTED));
            throw new UnsupportedOperationException();
        }

        if (groupByMixin.isEmpty()) {
            printList(todoList, scopedRepository);
        } else {
            final Map<String, ?> groupTodoListMap = todoList.stream().collect(groupByMixin.collector());
            printMap(groupTodoListMap, scopedRepository, groupByMixin, 0);
        }
        spec.commandLine().getOut().println(runningStats.getDuration());
    }
}
