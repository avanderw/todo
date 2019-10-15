package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.TodoReader;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.TodoItemFactory;
import net.avdw.todo.theme.ThemeApplicator;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Command(name = "start", description = "Start a todo item")
public class TodoStart implements Runnable {

    @ParentCommand
    private Todo todo;

    @Parameters(description = "Index to start", arity = "1")
    private int idx;

    @Inject
    private TodoReader reader;

    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    private TodoFileWriter todoFileWriter;
    @Inject
    private TodoItemFactory todoItemFactory;
    @Inject
    private SimpleDateFormat simpleDateFormat;
    @Inject
    private ThemeApplicator themeApplicator;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        System.out.println(themeApplicator.h1("todo:start"));

        List<TodoItem> allTodoItems = todoFileReader.readAll(todo.getTodoFile());
        if (idx > allTodoItems.size()) {
            Logger.warn(String.format("There are only '%s' items in the todo file and idx '%s' is too high", allTodoItems.size(), idx));
            return;
        } else if (idx <= 0) {
            Logger.warn(String.format("The idx '%s' cannot be negative", idx));
            return;
        }

        TodoItem todoItem = allTodoItems.get(idx - 1);
        Logger.info(String.format("Found  : %s", todoItem));

        if (todoItem.isComplete()) {
            Logger.warn("Item is already marked as done");
            return;
        }

        if (todoItem.isStarted()) {
            Logger.warn("Item is already started");
        }

        String changedRawValue = String.format("%s start:%s", todoItem.rawValue(), simpleDateFormat.format(new Date()));
        if (!todoItem.hasPriority()) {
            changedRawValue = String.format("(%s) %s", reader.readHighestFreePriority(todo.getTodoFile()).name(), changedRawValue);
        }

        TodoItem changedTodoItem = todoItemFactory.create(todoItem.getIdx(), changedRawValue);
        allTodoItems.set(idx - 1, changedTodoItem);
        todoFileWriter.write(allTodoItems, todo.getTodoFile());
        Logger.info(String.format("Started: %s", changedTodoItem));
        System.out.println(themeApplicator.hr());
    }
}
