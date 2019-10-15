package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.GlobalTodo;
import net.avdw.todo.LocalTodo;
import net.avdw.todo.Todo;
import net.avdw.todo.action.TodoAdd;
import net.avdw.todo.action.TodoRemove;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.item.TodoItem;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.nio.file.Path;
import java.util.List;

import static net.avdw.todo.render.ConsoleFormatting.h1;
import static net.avdw.todo.render.ConsoleFormatting.hr;

@Command(name = "migrate", description = "Move todo between local and global")
public class TodoMigrate implements Runnable {

    @ParentCommand
    private Todo todo;

    @Parameters(description = "Index to migrate", arity = "1")
    private int idx;

    @Inject
    private TodoAdd todoAdd;

    @Inject
    private TodoRemove todoRemove;
    @Inject
    private TodoFileReader todoFileReader;

    @Inject
    @GlobalTodo
    private Path globalPath;

    @Inject
    @LocalTodo
    private Path localPath;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        h1("todo:migrate");
        Path fromDirectory = todo.isGlobal() ? globalPath : localPath;
        Path toDirectory = todo.isGlobal() ? localPath : globalPath;
        Path fromFile = fromDirectory.resolve("todo.txt");
        Path toFile = toDirectory.resolve("todo.txt");

        List<TodoItem> allTodoItems = todoFileReader.readAll(fromFile);
        if (idx > allTodoItems.size()) {
            Logger.warn(String.format("There are only '%s' items in the todo file and idx '%s' is too high", allTodoItems.size(), idx));
            return;
        } else if (idx <= 0) {
            Logger.warn(String.format("The idx '%s' cannot be negative", idx));
            return;
        }

        TodoItem todoItem = allTodoItems.get(idx - 1);
        Logger.info(String.format("Migrate: %s", todoItem));
        todoRemove.remove(fromFile, idx);
        todoAdd.add(toFile, todoItem.rawValue());
        hr();
    }
}
