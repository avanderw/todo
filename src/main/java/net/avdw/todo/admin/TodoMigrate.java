package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.Console;
import net.avdw.todo.Todo;
import net.avdw.todo.TodoItem;
import net.avdw.todo.TodoReader;
import net.avdw.todo.action.TodoAdd;
import net.avdw.todo.action.TodoRemove;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.nio.file.Path;
import java.util.Optional;

@Command(name = "migrate", description = "Move todo between local and global")
public class TodoMigrate implements Runnable {

    @ParentCommand
    private Todo todo;

    @Parameters(description = "Index to migrate", arity = "1")
    private int idx;

    @Inject
    private TodoReader reader;

    @Inject
    private TodoAdd todoAdd;

    @Inject
    private TodoRemove todoRemove;

    @Override
    public void run() {
        Path fromDirectory = todo.global ? todo.globalPath : todo.localPath;
        Path toDirectory = todo.global ? todo.localPath : todo.globalPath;
        Path fromFile = fromDirectory.resolve("todo.txt");
        Path toFile = toDirectory.resolve("todo.txt");

        Optional<TodoItem> line = reader.readLine(fromFile, idx);
        if (line.isPresent()) {
            todoAdd.add(toFile, line.get().rawValue());
            todoRemove.remove(fromFile, idx);
            Console.info(String.format("Migrated line `%s` from `%s` to `%s`", line.get(), fromDirectory, toDirectory));
        } else {
            Console.error(String.format("Could not find index `%s` in `%s`", idx, fromDirectory));
        }
    }
}