package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import net.avdw.todo.style.StyleApplicator;
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
import java.util.function.Function;
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
    @Inject
    private RunningStats runningStats;

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

            List<Function<Todo, String>> groupByCollectorList = new ArrayList<>();
            if (!groupByList.isEmpty()) {
                groupByList.forEach(groupBy -> {
                    if (groupBy.startsWith("+")) {
                        String project = groupBy.substring(1);
                        if (project.isBlank()) {
                            Logger.debug("project 'all' ({})", project, groupBy);
                            groupByCollectorList.add(t -> {
                                if (t.getProjectList().isEmpty()) {
                                    return "";
                                }
                                if (t.getProjectList().size() > 1) {
                                    throw new UnsupportedOperationException();
                                } else {
                                    return t.getProjectList().get(0);
                                }
                            });
                        } else {
                            throw new UnsupportedOperationException();
                        }
                    } else if (groupBy.startsWith("@")) {
                        String context = groupBy.substring(1);
                        if (context.isBlank()) {
                            Logger.debug("context 'all' ({})", context, groupBy);
                            groupByCollectorList.add(t->{
                                if (t.getContextList().isEmpty()) {
                                    return "";
                                }
                                if (t.getContextList().size() > 1) {
                                    throw new UnsupportedOperationException();
                                } else {
                                    return t.getContextList().get(0);
                                }
                            });
                        } else {
                            throw new UnsupportedOperationException();
                        }
                    } else if (groupBy.endsWith(":")) {
                        String tag = groupBy.substring(0, groupBy.length() - 1);
                        Logger.debug("tag '{}' ({})", tag, groupBy);
                        groupByCollectorList.add(t->{
                            if (t.getTagValueList(tag).isEmpty()) {
                                return "";
                            }
                            if (t.getTagValueList(tag).size() > 1) {
                                throw new UnsupportedOperationException();
                            } else {
                                return t.getTagValueList(tag).get(0);
                            }
                        });
                    } else if (groupBy.contains(":")) {
                        String specificTag = groupBy.trim();
                        Logger.debug("specific {} ({})", specificTag, groupBy);
                    } else {
                        throw new UnsupportedOperationException();
                    }
                });
            }

            if (!groupByCollectorList.isEmpty()) {
                Logger.debug(todoList.stream().collect(Collectors.groupingBy(groupByCollectorList.get(0))).keySet());
            }

            todoList.forEach(todo -> {
                String todoText = isClean ? todoTextCleaner.clean(todo) : todo.getText();
                spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.TODO_LINE_ITEM,
                        String.format("{idx:'%3s',todo:\"%s\"}", todo.getIdx(), styleApplicator.apply(todoText).replaceAll("\"", "\\\\\""))));
            });

            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.TOTAL_SUMMARY,
                    String.format("{filtered:'%s',total:'%s'}", todoList.size(), repository.findAll(new Any<>()).size())));
            spec.commandLine().getOut().println(runningStats.getDuration());
        } catch (UnsupportedOperationException e) {
            Logger.debug(e);
            exitCode = 1;
        }
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
