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
import java.util.regex.Pattern;

@Command(name = "rm", description = "Remove a todo item")
public class TodoRemove implements Runnable {
    @ParentCommand
    private Todo todo;

    @Parameters(description = "Index to remove", arity = "1")
    private int idx;

    @Inject
    private TodoReader reader;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        Optional<TodoItem> line = remove(todo.getTodoFile(), idx);

        line.ifPresent(s -> Console.info(String.format("[%s%s%s] %sRemoved:%s %s",
                Ansi.BLUE, idx, Ansi.RESET,
                Ansi.RED, Ansi.RESET,
                s)));
    }

    /**
     * Remove a line from a text file.
     * The index to delete is relative to what is displayed.
     *
     * @param fromFile the file to remove the line index of
     * @param idx the todo index to find
     * @return the todo entry that was removed
     */
    public Optional<TodoItem> remove(final Path fromFile, final int idx) {
        Optional<TodoItem> line = reader.readLine(fromFile, idx);
        if (line.isPresent()) {
            try {
                String contents = new String(Files.readAllBytes(fromFile));
                Files.write(fromFile,
                        contents.replaceAll(String.format("%s\\r?\\n", Pattern.quote(line.get().rawValue())), "")
                                .getBytes());
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
