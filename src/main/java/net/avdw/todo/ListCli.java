package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.filters.BooleanFilter;
import net.avdw.todo.filters.DateFilter;
import net.avdw.todo.groupby.ContextGroupBy;
import net.avdw.todo.groupby.GroupBy;
import net.avdw.todo.groupby.ProjectGroupBy;
import net.avdw.todo.groupby.TagGroupBy;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import net.avdw.todo.style.StyleApplicator;
import org.codehaus.plexus.util.StringUtils;
import org.tinylog.Logger;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.IExitCodeGenerator;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Command(name = "ls", resourceBundle = "messages", description = "${bundle:list}")
public class ListCli implements Runnable, IExitCodeGenerator {
    @Mixin
    private BooleanFilter booleanFilter;
    @Mixin
    private DateFilter dateFilter;
    @ArgGroup
    private Exclusive exclusive = new Exclusive();
    private int exitCode = 0;
    @Option(names = "--group-by", descriptionKey = "list.group.by.desc", split = ",")
    private List<String> groupByList = new ArrayList<>();
    @Option(names = "--clean", descriptionKey = "list.clean.desc")
    private boolean isClean = false;
    @Inject
    private RunningStats runningStats;
    @Spec
    private CommandSpec spec;
    @Inject
    private StyleApplicator styleApplicator;
    @Inject
    private TemplatedResourceBundle templatedResourceBundle;
    @Inject
    private Path todoPath;
    @Inject
    private Repository<Integer, Todo> todoRepository;
    @Inject
    private TodoTextCleaner todoTextCleaner;

    private Collector<Todo, ?, Map<String, ?>> buildCollector(final List<Function<Todo, String>> groupByCollectorList) {
        Function f = groupByCollectorList.get(0);
        if (groupByCollectorList.size() > 1) {
            return Collectors.groupingBy(f, buildCollector(groupByCollectorList.subList(1, groupByCollectorList.size())));
        } else {
            return Collectors.groupingBy(f);
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    private void list(final Repository<Integer, Todo> repository) {
        try {
            Specification<Integer, Todo> specification = new Any<>();

            specification = dateFilter.specification(specification);
            specification = booleanFilter.specification(specification);

            Logger.debug(specification);
            List<Todo> todoList = repository.findAll(specification);
            if (todoList.isEmpty()) {
                spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.NO_TODO_FOUND));
                return;
            }

            List<GroupBy<Todo, String, String>> groupByList = new ArrayList<>();
            if (!this.groupByList.isEmpty()) {
                this.groupByList.forEach(selector -> {
                    GroupBy<Todo, String, String> projectGroupBy = new ProjectGroupBy();
                    GroupBy<Todo, String, String> contextGroupBy = new ContextGroupBy();
                    GroupBy<Todo, String, String> tagGroupBy = new TagGroupBy(selector.substring(0, selector.length() - 1));
                    if (projectGroupBy.isSatisfiedBy(selector)) {
                        Logger.debug("group-by project ({})", selector);
                        groupByList.add(projectGroupBy);
                    } else if (contextGroupBy.isSatisfiedBy(selector)) {
                        Logger.debug("group-by context ({})", selector);
                        groupByList.add(contextGroupBy);
                    } else if (tagGroupBy.isSatisfiedBy(selector)) {
                        Logger.debug("group-by tag ({})", selector);
                        groupByList.add(tagGroupBy);
                    } else {
                        throw new UnsupportedOperationException();
                    }
                });
            }

            if (groupByList.size() > 3) {
                spec.commandLine().getErr().println(templatedResourceBundle.getString(ResourceBundleKey.GROUP_BY_DEPTH_UNSUPPORTED));
                throw new UnsupportedOperationException();
            }

            if (groupByList.isEmpty()) {
                printList(todoList, repository);
            } else {
                Map<String, ?> groupTodoListMap = todoList.stream().collect(buildCollector(groupByList.stream().map(GroupBy::collector).collect(Collectors.toList())));
                printMap(groupTodoListMap, repository, groupByList, 0);
            }
            spec.commandLine().getOut().println(runningStats.getDuration());
        } catch (UnsupportedOperationException e) {
            Logger.debug(e);
            exitCode = 1;
        }
    }

    private void printList(final List<Todo> list, final Repository<Integer, Todo> repository) {
        list.forEach(todo -> {
            String todoText = isClean ? todoTextCleaner.clean(todo) : todo.getText();
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.TODO_LINE_ITEM,
                    String.format("{idx:'%3s',todo:\"%s\"}", todo.getIdx(), styleApplicator.apply(todoText).replaceAll("\"", "\\\\\""))));
        });

        spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.TOTAL_SUMMARY,
                String.format("{filtered:'%s',total:'%s'}", list.size(), repository.size())));
    }

    private void printMap(final Map<String, ?> map, final Repository<Integer, Todo> repository, final List<GroupBy<Todo, String, String>> hierarchyList, final int depth) {
        map.forEach((key, value) -> {
            String json = String.format("{type:'%s',title:'%s'}", hierarchyList.get(depth).name(), StringUtils.capitalise(key.isBlank() ? "No" : key));
            String header = switch (depth) {
                case 0 -> templatedResourceBundle.getString(ResourceBundleKey.GROUP_BY_HEADING, json);
                case 1 -> templatedResourceBundle.getString(ResourceBundleKey.GROUP_BY_HEADING_2, json);
                case 2 -> templatedResourceBundle.getString(ResourceBundleKey.GROUP_BY_HEADING_3, json);
                default -> throw new UnsupportedOperationException();
            };
            spec.commandLine().getOut().println(header);

            if (value instanceof Map) {
                printMap((Map) value, repository, hierarchyList, depth + 1);
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
        list(todoRepository);

        if (exclusive.inclDone) {
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.LIST_DONE_TITLE));
            list(new FileRepository<>(todoPath.getParent().resolve("done.txt"), new TodoFileTypeBuilder()));
        }

        if (exclusive.inclRemoved) {
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.LIST_REMOVED_TITLE));
            list(new FileRepository<>(todoPath.getParent().resolve("removed.txt"), new TodoFileTypeBuilder()));
        }

        if (exclusive.inclParked) {
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.LIST_PARKED_TITLE));
            list(new FileRepository<>(todoPath.getParent().resolve("parked.txt"), new TodoFileTypeBuilder()));
        }
    }

    private static class Exclusive {
        @Option(names = "--done", descriptionKey = "ls.done.desc")
        private boolean inclDone = false;
        @Option(names = "--parked", descriptionKey = "ls.parked.desc")
        private boolean inclParked = false;
        @Option(names = "--removed", descriptionKey = "ls.removed.desc")
        private boolean inclRemoved = false;
    }
}
