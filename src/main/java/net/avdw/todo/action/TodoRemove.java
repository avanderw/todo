package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.AnsiColor;
import net.avdw.todo.Todo;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.theme.ThemeApplicator;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

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
    @Inject
    private ThemeApplicator themeApplicator;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        System.out.println(themeApplicator.h1("todo:remove"));
        remove(todo.getTodoFile(), idx);
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
        allTodoItems.remove(idx - 1);
        todoFileWriter.write(allTodoItems, fromFile);
        Logger.info(String.format("%sRemoved%s: %s",
                AnsiColor.RED, AnsiColor.RESET,
                todoItem));
        return Optional.of(todoItem);
    }
}
