package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.Working;
import net.avdw.todo.file.TodoFile;
import net.avdw.todo.file.TodoFileFactory;
import net.avdw.todo.file.TodoFileReader;
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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


@Command(name = "ls", description = "List the items in todo.txt")
public class TodoList implements Runnable {
    @Inject
    @ParentCommand
    private Todo todo;

    @Parameters(description = "Include items that contain these Strings")
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

    @Option(names = "--not", description = "Exclude items with this String")
    private List<String> notStringList = new ArrayList<>();
    @Option(names = "--and", description = "Include items that contain this String with the filter")
    private List<String> andStringList = new ArrayList<>();
    @Option(names = "--or", description = "Include items that also has this String")
    private List<String> orStringList = new ArrayList<>();

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
    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    @Working
    private Path todoPath;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        andStringList.addAll(filters);
        List<TodoItem> todoItemList = todoFileReader.readAll(todoPath);
        List<TodoItem> filteredTodoItemList = new ArrayList<>();
        List<TodoItem> finalFilteredTodoItemList = filteredTodoItemList;
        todoItemList.forEach(item -> {
            String rawValue = item.getRawValue().toLowerCase();
            boolean include = andStringList.isEmpty() || andStringList.stream().map(String::toLowerCase).allMatch(rawValue::contains);
            if (!include && !orStringList.isEmpty()) {
                include = orStringList.stream().map(String::toLowerCase).anyMatch(rawValue::contains);
            }
            if (include && !notStringList.isEmpty()) {
                include = notStringList.stream().map(String::toLowerCase).noneMatch(rawValue::contains);
            }
            if (include) {
                finalFilteredTodoItemList.add(item);
            }
        });

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

        TodoFile fileBefore = todoFileFactory.create(todo.getTodoFile());
        TemplateViewModel templateViewModel = new TemplateViewModel("list", filteredTodoItemList, fileBefore, fileBefore);
        System.out.println(templateExecutor.executor(templateViewModel));
    }
}
