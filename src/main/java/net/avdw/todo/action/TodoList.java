package net.avdw.todo.action;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.render.TodoContextRenderer;
import net.avdw.todo.render.TodoProjectRenderer;
import net.avdw.todo.theme.ThemeApplicator;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private int limit = 0;

    @Inject
    private TodoContextRenderer todoContextRenderer;

    @Inject
    private TodoProjectRenderer todoProjectRenderer;

    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    private ThemeApplicator themeApplicator;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        List<TodoItem> allTodoItems = todoFileReader.readAll(todo.getTodoFile());
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

        if (limit != 0) {
            filteredTodoItems = filteredTodoItems.subList(0, limit);
        }

        if (filteredTodoItems.isEmpty()) {
            Logger.info("The list is empty");
        }

        if (displayContexts) {
            todoContextRenderer.printContextTable(filteredTodoItems);
        }
        if (displayProjects) {
            todoProjectRenderer.printProjectTable(filteredTodoItems);
        }

        ListModel model = new ListModel();
        Map<String, Object> context = new HashMap<>();
        context.put("theme", themeApplicator);
        context.put("model", model);

        model.filteredTodoItems = filteredTodoItems;
        model.allTodoItems = allTodoItems;
        model.completedTodoItems = allTodoItems.stream().filter(TodoItem::isComplete).collect(Collectors.toList());

        Mustache m = new DefaultMustacheFactory().compile("todo-list.mustache");
        StringWriter writer = new StringWriter();
        m.execute(writer, context);
        System.out.println(writer.toString());
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

    /**
     * List priority items.
     */
    public void listPriorities(final Path todoFile) {
        List<TodoItem> allTodoItems = todoFileReader.readAll(todoFile);
        List<TodoItem> filteredTodoItems = filterPriorityItems(allTodoItems);

        ListModel model = new ListModel();
        Map<String, Object> context = new HashMap<>();
        context.put("theme", themeApplicator);
        context.put("model", model);

        model.filteredTodoItems = filteredTodoItems;
        model.allTodoItems = allTodoItems;
        model.completedTodoItems = allTodoItems.stream().filter(TodoItem::isComplete).collect(Collectors.toList());

        Mustache m = new DefaultMustacheFactory().compile("todo-priority.mustache");
        StringWriter writer = new StringWriter();
        m.execute(writer, context);
        System.out.println(writer.toString());
    }

    private static class ListModel {
        private List<TodoItem> completedTodoItems;
        private List<TodoItem> allTodoItems;
        private List<TodoItem> filteredTodoItems;

        public List<TodoItem> getFilteredTodoItems() {
            return filteredTodoItems;
        }

        public List<TodoItem> getAllTodoItems() {
            return allTodoItems;
        }

        public List<TodoItem> getCompletedTodoItems() {
            return completedTodoItems;
        }
    }
}
