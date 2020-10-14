package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
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
import java.util.Locale;
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

    private Collector<Todo, ?, Map<String, ?>> buildCollector(final List groupByCollectorList) {
        for (int i = 0; i < groupByCollectorList.size(); i++) {
            Function f = (Function) groupByCollectorList.get(i);
            if (i + 1 < groupByCollectorList.size()) {
                return Collectors.groupingBy(f, buildCollector(groupByCollectorList.subList(i + 1, groupByCollectorList.size())));
            } else {
                return Collectors.groupingBy(f);
            }
        }
        throw new UnsupportedOperationException();
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

            List<String> hierarchyList = new ArrayList<>();
            List<Function<Todo, String>> groupByCollectorList = new ArrayList<>();
            if (!groupByList.isEmpty()) {
                groupByList.forEach(groupBy -> {
                    if (groupBy.startsWith("+") || groupBy.toLowerCase(Locale.ENGLISH).equals("project")) {
                        hierarchyList.add("project");
                        String project = groupBy.substring(1);
                        if (project.isBlank() || project.equals("roject")) {
                            Logger.debug("group-by project 'all' ({})", project, groupBy);
                            groupByCollectorList.add(t -> {
                                if (t.getProjectList().isEmpty()) {
                                    return "";
                                }
                                if (t.getProjectList().size() > 1) {
                                    return String.join(", ", t.getProjectList());
                                } else {
                                    return t.getProjectList().get(0);
                                }
                            });
                        } else {
                            throw new UnsupportedOperationException();
                        }
                    } else if (groupBy.startsWith("@") || groupBy.toLowerCase(Locale.ENGLISH).equals("context")) {
                        hierarchyList.add("context");
                        String context = groupBy.substring(1);
                        if (context.isBlank() || context.equals("ontext")) {
                            Logger.debug("group-by context 'all' ({})", context, groupBy);
                            groupByCollectorList.add(t -> {
                                if (t.getContextList().isEmpty()) {
                                    return "";
                                }
                                if (t.getContextList().size() > 1) {
                                    return String.join(", ", t.getContextList());
                                } else {
                                    return t.getContextList().get(0);
                                }
                            });
                        } else {
                            throw new UnsupportedOperationException();
                        }
                    } else if (groupBy.endsWith(":")) {
                        String tag = groupBy.substring(0, groupBy.length() - 1);
                        hierarchyList.add(tag);
                        Logger.debug("group-by tag '{}' ({})", tag, groupBy);
                        groupByCollectorList.add(t -> {
                            if (t.getTagValueList(tag).isEmpty()) {
                                return "";
                            }
                            if (t.getTagValueList(tag).size() > 1) {
                                return String.join(", ", t.getTagValueList(tag));
                            } else {
                                return t.getTagValueList(tag).get(0);
                            }
                        });
                    } else {
                        throw new UnsupportedOperationException();
                    }
                });
            }

            if (groupByCollectorList.size() > 3) {
                spec.commandLine().getErr().println(templatedResourceBundle.getString(ResourceBundleKey.GROUP_BY_DEPTH_UNSUPPORTED));
                throw new UnsupportedOperationException();
            }

            if (groupByCollectorList.isEmpty()) {
                printList(todoList, repository);
            } else {
                Map groupTodoListMap = todoList.stream().collect(buildCollector(groupByCollectorList));
                printMap(groupTodoListMap, repository, hierarchyList, 0);
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

    private void printMap(final Map<String, ?> map, final Repository<Integer, Todo> repository, final List<String> hierarchyList, final int depth) {
        map.forEach((key, value) -> {
            String json = String.format("{type:'%s',title:'%s'}", hierarchyList.get(depth), StringUtils.capitalise(key.isBlank() ? "No" : key));
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
