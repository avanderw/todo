package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.ext.StartedExt;
import net.avdw.todo.filters.BooleanFilterMixin;
import net.avdw.todo.filters.DateFilterMixin;
import net.avdw.todo.groupby.GroupByMixin;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import net.avdw.todo.stats.StatisticMixin;
import net.avdw.todo.style.StyleApplicator;
import org.codehaus.plexus.util.StringUtils;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.IExitCodeGenerator;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.util.List;
import java.util.Map;

@Command(name = "ls", resourceBundle = "messages", description = "${bundle:list}", mixinStandardHelpOptions = true)
public class ListCli implements Runnable, IExitCodeGenerator {
    @Mixin
    private BooleanFilterMixin booleanFilterMixin;
    @Mixin
    private DateFilterMixin dateFilterMixin;
    private int exitCode = 0;
    @Mixin
    private GroupByMixin groupByMixin;
    @Option(names = "--clean", descriptionKey = "list.clean.desc")
    private boolean isClean = false;
    @Mixin
    private RepositoryMixin repositoryMixin;
    @Inject
    private RunningStats runningStats;
    @Spec
    private CommandSpec spec;
    @Inject
    private StyleApplicator styleApplicator;
    @Inject
    private TemplatedResource templatedResource;
    @Inject
    private TodoTextCleaner todoTextCleaner;
    @Mixin
    private StatisticMixin statisticMixin;
    @Inject
    private StartedExt startedExt;

    @Override
    public int getExitCode() {
        return exitCode;
    }

    private void printList(final List<Todo> list, final Repository<Integer, Todo> repository) {
        list.forEach(todo -> {
            String todoText = isClean ? todoTextCleaner.clean(todo) : todo.getText();
            spec.commandLine().getOut().println(templatedResource.populate(ResourceBundleKey.TODO_LINE_ITEM,
                    String.format("{idx:'%3s',todo:\"%s\"}", todo.getIdx(), styleApplicator.apply(todoText).replaceAll("\"", "\\\\\""))));
        });

        spec.commandLine().getOut().println(templatedResource.populate(ResourceBundleKey.TOTAL_SUMMARY,
                String.format("{filtered:'%s',total:'%s',todo:'%s',started:'%s',done:'%s'}", list.size(), repository.size(),
                list.stream().filter(startedExt::notStarted).count(),
                list.stream().filter(startedExt::started).count(),
                list.stream().filter(Todo::isDone).count())));

        spec.commandLine().getOut().print(statisticMixin.renderStats(list));
    }

    private void printMap(final Map<String, ?> map, final Repository<Integer, Todo> repository, final GroupByMixin groupByMixin, final int depth) {
        map.forEach((key, value) -> {
            String json = String.format("{type:'%s',title:'%s'}", groupByMixin.getGroupByAtDepth(depth).name(), StringUtils.capitalise(key.isBlank() ? "No" : key));
            String header = switch (depth) {
                case 0 -> templatedResource.populate(ResourceBundleKey.GROUP_BY_HEADING, json);
                case 1 -> templatedResource.populate(ResourceBundleKey.GROUP_BY_HEADING_2, json);
                case 2 -> templatedResource.populate(ResourceBundleKey.GROUP_BY_HEADING_3, json);
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
            spec.commandLine().getOut().println(templatedResource.populate(ResourceBundleKey.NO_TODO_FOUND));
            return;
        }

        groupByMixin.setup();
        if (groupByMixin.depth() > 3) {
            spec.commandLine().getErr().println(templatedResource.populate(ResourceBundleKey.GROUP_BY_DEPTH_UNSUPPORTED));
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
