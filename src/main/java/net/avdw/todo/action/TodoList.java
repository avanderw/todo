package net.avdw.todo.action;

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
    @ParentCommand
    private Todo todo;

    @Parameters(description = "One or more filters to apply")
    private List<String> filters;

    @Option(names = {"-p", "--projects"}, description = "List projects")
    private boolean showProjects;

    @Option(names = {"-c", "--contexts"}, description = "List contexts")
    private boolean showContexts;

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
            Set<String> projects = new HashSet<>();
            Set<String> contexts = new HashSet<>();
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                TodoItem item = new TodoItem(line);

                if (item.isNotDone() || todo.showAll()) {
                    lineNum++;
                    if (item.isDone()) {
                        completed++;
                    }
                    if (filters.stream().map(String::toLowerCase).allMatch(line.toLowerCase()::contains)) {
                        matched++;
                        projects.addAll(item.getProjects());
                        contexts.addAll(item.getContexts());
                        if (!(showProjects || showContexts)) {
                            Console.info(String.format("[%s%2s%s] %s", Ansi.Blue, lineNum, Ansi.Reset, item));
                        }
                    }
                }
            }

            Console.divide();
            if (todo.showAll()) {
                Console.info(String.format("%s of %s (%s done) tasks", matched, lineNum, completed));
            } else {
                Console.info(String.format("%s of %s tasks", matched, lineNum));
            }
            Console.info(String.format("projects: %s", projects));
            Console.info(String.format("contexts: %s", contexts));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
