package net.avdw.todo.action;

import net.avdw.todo.Ansi;
import net.avdw.todo.Console;
import net.avdw.todo.Todo;
import net.avdw.todo.TodoItem;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Command(name = "rm", description = "Remove a todo item")
public class TodoRemove implements Runnable {
    @ParentCommand
    private Todo todo;

    @Parameters(description = "Index to remove", arity = "1")
    private int idx;

    @Override
    public void run() {
        Optional<TodoItem> line = new TodoReader(todo).readLine(idx);
        if (line.isPresent()) {
            try {
                String contents = new String(Files.readAllBytes(todo.getTodoFile()));
                Files.write(todo.getTodoFile(),
                        contents.replace(String.format("%s%n",line.get().rawValue()), "").getBytes());

                Console.info(String.format("[%s%s%s] %sRemoved:%s %s",
                        Ansi.Blue, idx, Ansi.Reset,
                        Ansi.Red, Ansi.Reset,
                        line.get()));
            } catch (IOException e) {
                Console.error(String.format("Error writing `%s`", todo.getTodoFile()));
                Logger.error(e);
            }
        } else {
            Console.error(String.format("Could not find index (%s)", idx));
        }
    }
}
