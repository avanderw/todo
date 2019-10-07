package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Ansi;
import net.avdw.todo.Console;
import net.avdw.todo.Todo;
import net.avdw.todo.TodoItem;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.util.*;


@Command(name = "ls", description = "List the items in todo.txt")
public class TodoList implements Runnable {
    @Inject
    @ParentCommand
    private Todo todo;

    @Parameters(description = "One or more filters to apply")
    private List<String> filters;

    @Option(names = {"-p", "--projects"}, description = "List projects")
    private boolean showProjects;

    @Option(names = {"-c", "--contexts"}, description = "List contexts")
    private boolean showContexts;

    @Option(names = {"-w", "--in-progress"}, description = "List in progress items")
    private boolean inProgress;

    @Option(names = "--priority", description = "List priority items")
    private boolean onlyPriority;

    @Option(names = {"-l", "--limit"}, description = "Limit the amount of items shown")
    private int limit = Integer.MAX_VALUE;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        Logger.debug(String.format("Filters: %s", filters));
        if (filters == null) {
            filters = new ArrayList<>();
        }

        try (Scanner scanner = new Scanner(todo.getTodoFile())) {
            int lineNum = 0;
            int matched = 0;
            int completed = 0;
            Map<String, Integer> projects = new HashMap<>();
            Map<String, Integer> contexts = new HashMap<>();
            while (scanner.hasNext() && matched < limit) {
                String line = scanner.nextLine();
                TodoItem item = new TodoItem(line);

                if (item.isNotDone() || todo.showAll()) {
                    lineNum++;
                    if (item.isDone()) {
                        completed++;
                    }
                    if (filters.stream().map(String::toLowerCase).allMatch(line.toLowerCase()::contains)) {
                        item.getProjects().forEach(project -> {
                            projects.putIfAbsent(project, 0);
                            projects.put(project, projects.get(project) + 1);
                        });
                        item.getContexts().forEach(context -> {
                            contexts.putIfAbsent(context, 0);
                            contexts.put(context, contexts.get(context) + 1);
                        });

                        if (onlyPriority && inProgress) {
                            if (item.hasPriority() && item.isInProgress()) {
                                matched++;
                                if (!(showProjects || showContexts)) {
                                    Console.info(String.format("[%s%2s%s] %s", Ansi.BLUE, lineNum, Ansi.RESET, item));
                                }
                            }
                        } else if (onlyPriority) {
                            if (item.hasPriority()) {
                                matched++;
                                if (!(showProjects || showContexts)) {
                                    Console.info(String.format("[%s%2s%s] %s", Ansi.BLUE, lineNum, Ansi.RESET, item));
                                }
                            }
                        } else if (inProgress) {
                            if (item.isInProgress()) {
                                matched++;
                                if (!(showProjects || showContexts)) {
                                    Console.info(String.format("[%s%2s%s] %s", Ansi.BLUE, lineNum, Ansi.RESET, item));
                                }
                            }
                        } else {
                            matched++;
                            if (!(showProjects || showContexts)) {
                                Console.info(String.format("[%s%2s%s] %s", Ansi.BLUE, lineNum, Ansi.RESET, item));
                            }
                        }
                    }
                }
            }

            Console.divide();
            if (todo.showAll()) {
                if (matched == limit) {
                    Console.info(String.format("TODO: %s (%s done) of ... (limited to %s) tasks shown", matched, completed, limit));
                } else {
                    Console.info(String.format("TODO: %s (%s done) of %s tasks shown", matched, completed, lineNum));
                }
            } else {
                if (matched == limit) {
                    Console.info(String.format("TODO: %s of ... (limited to %s) tasks shown", matched, limit));
                } else {
                    Console.info(String.format("TODO: %s of %s tasks shown", matched, lineNum));
                }
            }
            if (showProjects) {
                Console.info(String.format("projects: %s", projects));
            }
            if (showContexts) {
                Console.info(String.format("contexts: %s", contexts));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
