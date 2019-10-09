package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.TodoItemV1;
import net.avdw.todo.item.TodoItem;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

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

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        h1("todo:repeat");
        Optional<TodoItem> doneItem = todoDone.done(todo.getTodoFile(), idx);
        doneItem.ifPresent(todoItem -> {
            String rawValue = todoItem.rawValue();
            if (todoItem.hasPriority()) {
                rawValue = rawValue.replaceFirst("\\([A-Z]\\) \\d\\d\\d\\d-\\d\\d-\\d\\d", simpleDateFormat.format(new Date()));
            } else {
                rawValue = rawValue.replaceFirst("\\d\\d\\d\\d-\\d\\d-\\d\\d", simpleDateFormat.format(new Date()));
            }
            rawValue = rawValue.replaceAll("due:\\d\\d\\d\\d-\\d\\d-\\d\\d", String.format("due:%s", simpleDateFormat.format(dueDate)));
            todoAdd.add(todo.getTodoFile(), rawValue);
            Logger.info(String.format("Added: %s", new TodoItemV1(rawValue)));
        });
    }
}
