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
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;


@Command(name = "ls", description = "List the items in todo.txt")
public class TodoList implements Runnable {
    @Inject
    @ParentCommand
    private Todo todo;

    @Parameters(description = "One or more filters to apply")
    private List<String> filters = new ArrayList<>();

    @Option(names = "--projects", description = "Display projects")
    private boolean displayProjects;

    @Option(names = "--contexts", description = "Display contexts")
    private boolean displayContexts;

    @Option(names = "--in-progress", description = "Filter in-progress items")
    private boolean filterInProgress;

    @Option(names = "--priority", description = "Filter priority items")
    private boolean filterPriority;

    @Option(names = "--limit", description = "Limit the amount of items shown")
    private int limit = Integer.MAX_VALUE;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        List<TodoItem> allTodoItems = readAllTodoItems(todo.getTodoFile());
        List<TodoItem> filteredTodoItems = filterTodoItems(allTodoItems, filters);

        if (!todo.showAll()) {
            filteredTodoItems = filterIncompleteItems(filteredTodoItems);
        }

        if (filterInProgress) {
            filteredTodoItems = filterInProgressTodoItems(filteredTodoItems);
        }

        if (filterPriority) {
            filteredTodoItems = filterPriorityItems(filteredTodoItems);
        }

        if (filteredTodoItems.isEmpty()) {
            Logger.info("The list is empty");
        }

        for (int i = 0; i < filteredTodoItems.size() && i < limit; i++) {
            TodoItem item = filteredTodoItems.get(i);
            Console.info(String.format("[%s%2s%s] %s", Ansi.BLUE, item.getIdx(), Ansi.RESET, item));
        }

        Console.divide();
        long completed = allTodoItems.stream().filter(TodoItem::isDone).count();
        Logger.info(String.format("%s of %s (%s done) todo items shown", filteredTodoItems.size(), allTodoItems.size(), completed));

        listProjects(filteredTodoItems);
        listContexts(filteredTodoItems);
    }

    private List<TodoItem> filterPriorityItems(final List<TodoItem> todoItemList) {
        Logger.debug(String.format("Filtering priority items from '%s' todo items", todoItemList.size()));
        List<TodoItem> filteredTodoItems = todoItemList.stream()
                .filter(TodoItem::hasPriority)
                .collect(Collectors.toList());
        Logger.debug(String.format("Filtered list contains '%s' todo items", filteredTodoItems.size()));
        return filteredTodoItems;
    }

    private List<TodoItem> filterInProgressTodoItems(final List<TodoItem> todoItemList) {
        Logger.debug(String.format("Filtering in-progress items from '%s' todo items", todoItemList.size()));
        List<TodoItem> filteredTodoItems = todoItemList.stream()
                .filter(TodoItem::isInProgress)
                .collect(Collectors.toList());
        Logger.debug(String.format("Filtered list contains '%s' todo items", filteredTodoItems.size()));
        return filteredTodoItems;
    }

    private List<TodoItem> filterIncompleteItems(final List<TodoItem> todoItemList) {
        Logger.debug(String.format("Filtering done items from '%s' todo items", todoItemList.size()));
        List<TodoItem> filteredTodoItems = todoItemList.stream()
                .filter(TodoItem::isIncomplete)
                .collect(Collectors.toList());
        Logger.debug(String.format("Filtered list contains '%s' todo items", filteredTodoItems.size()));
        return filteredTodoItems;

    }

    private void listContexts(final List<TodoItem> todoItemList) {
        if (displayContexts) {
            Map<String, Integer> contexts = new HashMap<>();
            todoItemList.forEach(todoItem
                    -> todoItem.getContexts().forEach(context -> {
                contexts.putIfAbsent(context, 0);
                contexts.computeIfPresent(context, (key, value) -> value + 1);
            }));

            Logger.info(String.format("contexts: %s", contexts));
        }
    }

    private void listProjects(final List<TodoItem> todoItemList) {
        if (displayProjects) {
            Map<String, Integer> projects = new HashMap<>();
            todoItemList.forEach(todoItem
                    -> todoItem.getProjects().forEach(project -> {
                projects.putIfAbsent(project, 0);
                projects.computeIfPresent(project, (key, value) -> value + 1);
            }));

            Logger.info(String.format("projects: %s", projects));
        }
    }

    private List<TodoItem> filterTodoItems(final List<TodoItem> todoItemList, final List<String> filters) {
        Logger.debug(String.format("Filtering '%s' todo items with filters '%s'", todoItemList.size(), filters));
        if (filters.isEmpty()) {
            Logger.debug("No filters defined, returning original list");
            return new ArrayList<>(todoItemList);
        } else {
            List<TodoItem> filteredTodoItems = todoItemList.stream()
                    .filter(item -> filters.stream()
                            .map(String::toLowerCase)
                            .allMatch(item.rawValue().toLowerCase()::contains))
                    .collect(Collectors.toList());
            Logger.debug(String.format("Filtered list contains '%s' todo items", filteredTodoItems.size()));
            return filteredTodoItems;
        }
    }

    private List<TodoItem> readAllTodoItems(final Path todoFile) {
        Logger.debug(String.format("Reading all todo.txt items from '%s'", todoFile));
        List<TodoItem> todoItemList = new ArrayList<>();
        try (Scanner scanner = new Scanner(todoFile)) {
            while (scanner.hasNextLine()) {
                todoItemList.add(new TodoItem(todoItemList.size() + 1, scanner.nextLine()));
            }
        } catch (IOException e) {
            Logger.error(String.format("Could not read '%s' because %s", todoFile, e.getMessage()));
            Logger.debug(e);
        }
        return todoItemList;
    }
}
