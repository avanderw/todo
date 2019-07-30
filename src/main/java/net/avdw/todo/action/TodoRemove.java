package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.*;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Command(name = "rm", description = "Remove a todo item")
public class TodoRemove implements Runnable {
    @ParentCommand
    private Todo todo;

    @Parameters(description = "Index to remove", arity = "1")
    private int idx;

    @Inject
    private TodoReader reader;

    @Override
    public void run() {
        Optional<TodoItem> line = remove(todo.getTodoFile(), idx);

        line.ifPresent(s -> Console.info(String.format("[%s%s%s] %sRemoved:%s %s",
                Ansi.Blue, idx, Ansi.Reset,
                Ansi.Red, Ansi.Reset,
                s)));
    }

    public Optional<TodoItem> remove(Path fromFile, int idx) {
        Optional<TodoItem> line = reader.readLine(fromFile, idx);
        if (line.isPresent()) {
            try {
                String contents = new String(Files.readAllBytes(fromFile));
                Files.write(fromFile,
                        contents.replace(String.format("%s%n", line.get().rawValue()), "").getBytes());
            } catch (IOException e) {
                Console.error(String.format("Error writing `%s`", fromFile));
                Logger.error(e);
            }
        } else {
            Console.error(String.format("Could not find index (%s)", idx));
        }

        return line;
    }
}
