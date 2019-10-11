package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Ansi;
import net.avdw.todo.Todo;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static net.avdw.todo.render.ConsoleFormatting.h1;

@Command(name = "rm", description = "Remove a todo item")
public class TodoRemove implements Runnable {
    @ParentCommand
    private Todo todo;
    @Parameters(description = "Index to remove", arity = "1")
    private int idx;
    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    private TodoFileWriter todoFileWriter;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        h1("todo:remove");
        Optional<TodoItem> line = remove(todo.getTodoFile(), idx);

        line.ifPresent(s -> Logger.info(String.format("%sRemoved:%s %s",
                Ansi.RED, Ansi.RESET,
                s)));
    }

    /**
     * Remove a line from a text file.
     * The index to delete is relative to what is displayed.
     *
     * @param fromFile the file to remove the line index of
     * @param idx      the todo index to find
     * @return the todo entry that was removed
     */
    public Optional<TodoItem> remove(final Path fromFile, final int idx) {
        List<TodoItem> allTodoItems = todoFileReader.readAll(fromFile);
        if (idx > allTodoItems.size()) {
            Logger.warn(String.format("There are only '%s' items in the todo file and idx '%s' is too high", allTodoItems.size(), idx));
            return Optional.empty();
        } else if (idx <= 0) {
            Logger.warn(String.format("The idx '%s' cannot be negative", idx));
            return Optional.empty();
        }

        TodoItem todoItem = allTodoItems.get(idx - 1);
        Logger.info(todoItem);
        allTodoItems.remove(idx - 1);
        todoFileWriter.write(allTodoItems, fromFile);
        return Optional.of(todoItem);
    }
}
