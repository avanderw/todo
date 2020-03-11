package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.file.TodoFile;
import net.avdw.todo.file.TodoFileFactory;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.list.TodoItemListFilter;
import net.avdw.todo.render.TodoContextRenderer;
import net.avdw.todo.render.TodoProjectRenderer;
import net.avdw.todo.template.TemplateExecutor;
import net.avdw.todo.template.TemplateViewModel;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.util.ArrayList;
import java.util.List;
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
    private TemplateExecutor templateExecutor;
    @Inject
    private TodoFileFactory todoFileFactory;
    @Inject
    private TodoItemListFilter todoItemListFilter;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        TodoFile fileBefore = todoFileFactory.create(todo.getTodoFile());
        List<TodoItem> filteredTodoItemList = filterTodoItems(fileBefore.getTodoItemList().getAll(), filters);

        if (!todo.showAll()) {
            filteredTodoItemList = todoItemListFilter.filterIncompleteItems(filteredTodoItemList);
        }

        if (filterInProgress) {
            filteredTodoItemList = todoItemListFilter.filterInProgressTodoItems(filteredTodoItemList);
        }

        if (filterPriority) {
            filteredTodoItemList = todoItemListFilter.filterPriorityItems(filteredTodoItemList);
        }

        if (limit != 0) {
            filteredTodoItemList = filteredTodoItemList.subList(0, limit);
        }

        if (filteredTodoItemList.isEmpty()) {
            Logger.info("The list is empty");
        }

        if (displayContexts) {
            todoContextRenderer.printContextTable(filteredTodoItemList);
        }
        if (displayProjects) {
            todoProjectRenderer.printProjectTable(filteredTodoItemList);
        }

        TemplateViewModel templateViewModel = new TemplateViewModel("list", filteredTodoItemList, fileBefore, fileBefore);
        System.out.println(templateExecutor.executor(templateViewModel));
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
                            .allMatch(item.getRawValue().toLowerCase()::contains))
                    .collect(Collectors.toList());
            Logger.debug(String.format("Filtered list contains '%s' todo items", filteredTodoItems.size()));
            return filteredTodoItems;
        }
    }
}
