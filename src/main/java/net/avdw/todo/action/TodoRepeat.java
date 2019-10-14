package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Ansi;
import net.avdw.todo.Todo;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.TodoItemFactory;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static net.avdw.todo.render.ConsoleFormatting.h1;

@Command(name = "repeat", description = "Do and add an entry to todo.txt")
public class TodoRepeat implements Runnable {

    @ParentCommand
    private Todo todo;

    @Parameters(description = "Index of the entry to remove", arity = "1", index = "0")
    private int idx;

    @Parameters(description = "Due date to add with the new entry", arity = "1", index = "1")
    private Date dueDate;

    @Inject
    private TodoDone todoDone;

    @Inject
    private TodoAdd todoAdd;

    @Inject
    private SimpleDateFormat simpleDateFormat;
    @Inject
    private TodoItemFactory todoItemFactory;
    @Inject
    private TodoFileReader todoFileReader;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        h1("todo:repeat");
        List<TodoItem> allTodoItems = todoFileReader.readAll(todo.getTodoFile());
        if (idx > allTodoItems.size()) {
            Logger.warn(String.format("There are only '%s' items in the todo file and idx '%s' is too high", allTodoItems.size(), idx));
            return;
        } else if (idx <= 0) {
            Logger.warn(String.format("The idx '%s' cannot be negative", idx));
            return;
        }

        todoDone.complete(todo.getTodoFile(), idx);

        TodoItem toCompleteTodoItem = allTodoItems.get(idx - 1);
        String rawValue = toCompleteTodoItem.rawValue().replaceFirst("^x \\d\\d\\d\\d-\\d\\d-\\d\\d\\s", "");
        rawValue = rawValue.replaceFirst("^\\([A-Z]\\)\\s", "");
        rawValue = rawValue.replaceFirst("^\\d\\d\\d\\d-\\d\\d-\\d\\d\\s", "");
        rawValue = rawValue.replaceAll("due:\\d\\d\\d\\d-\\d\\d-\\d\\d", String.format("due:%s", simpleDateFormat.format(dueDate)));
        rawValue = String.format("%s %s", simpleDateFormat.format(new Date()), rawValue);
        todoAdd.add(todo.getTodoFile(), rawValue);
        Logger.info(String.format("%sAdded%s: %s", Ansi.GREEN, Ansi.RESET, todoItemFactory.create(allTodoItems.size() + 1, rawValue)));
    }
}
