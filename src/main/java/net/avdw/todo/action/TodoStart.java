package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.TodoReader;
import net.avdw.todo.file.TodoFile;
import net.avdw.todo.file.TodoFileFactory;
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

@Command(name = "start", description = "Start a todo item")
public class TodoStart implements Runnable {

    @ParentCommand
    private Todo todo;

    @Parameters(description = "Index to start", arity = "1")
    private int idx;

    @Inject
    private TodoReader reader;

    @Inject
    private TodoFileFactory todoFileFactory;
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
        System.out.println(themeApplicator.header("todo:start"));

        TodoFile todoFile = todoFileFactory.create(todo.getTodoFile());
        if (idx > todoFile.getTodoItemList().getAll().size()) {
            Logger.warn(String.format("There are only '%s' items in the todo file and idx '%s' is too high", todoFile.getTodoItemList().getAll().size(), idx));
            return;
        } else if (idx <= 0) {
            Logger.warn(String.format("The idx '%s' cannot be negative", idx));
            return;
        }

        TodoItem todoItem = todoFile.getTodoItemList().getAll().get(idx - 1);
        Logger.info(String.format("Found  : %s", todoItem));

        if (todoItem.isComplete()) {
            Logger.warn("Item is already marked as done");
            return;
        }

        if (todoItem.isStarted()) {
            Logger.warn("Item is already started");
        }

        String changedRawValue = String.format("%s start:%s", todoItem.getRawValue(), simpleDateFormat.format(new Date()));
        if (!todoItem.hasPriority()) {
            changedRawValue = String.format("(%s) %s", reader.readHighestFreePriority(todo.getTodoFile()).name(), changedRawValue);
        }

        TodoItem changedTodoItem = todoItemFactory.create(todoItem.getIdx(), changedRawValue);
        todoFile.getTodoItemList().getAll().set(idx - 1, changedTodoItem);
        todoFileWriter.write(todoFile);
        Logger.info(String.format("Started: %s", changedTodoItem));
        System.out.println(themeApplicator.hr());
    }
}
