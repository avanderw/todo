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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Command(name = "do", description = "Complete a todo item")
public class TodoDone implements Runnable {
    @ParentCommand
    private Todo todo;

    @Parameters(description = "Index to complete", arity = "1")
    private int idx;

    @Inject
    private TodoReader reader;

    @Inject
    private SimpleDateFormat simpleDateFormat;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        done(todo.getTodoFile(), idx);
    }

    /**
     * Find and mark an entry in a file at idx as done.
     * @param todoFile the file to search
     * @param idx the entry in the file to complete
     * @return the original line that was marked as done
     */
    Optional<TodoItem> done(final Path todoFile, final int idx) {
        Optional<TodoItem> line = reader.readLine(todoFile, idx);
        if (line.isPresent() && line.get().isNotDone()) {
            try {
                String completeLine = String.format("x %s %s",
                        simpleDateFormat.format(new Date()),
                        line.get().rawValue().replaceFirst("\\([A-Z]\\) ", ""));

                String contents = new String(Files.readAllBytes(todoFile));
                Files.write(todoFile, contents.replace(line.get().rawValue(), completeLine).getBytes());

                Console.info(String.format("[%s%s%s]: %s", Ansi.BLUE, idx, Ansi.RESET, line.get()));
                Console.divide();
                Console.info(String.format("[%s%s%s]: %s", Ansi.BLUE, idx, Ansi.RESET, new TodoItem(completeLine)));
            } catch (IOException e) {
                Console.error(String.format("Error writing `%s`", todoFile));
                Logger.error(e);
            }
        } else if (line.isPresent() && line.get().isDone()) {
            Console.info(String.format("[%s%s%s] %s",
                    Ansi.BLUE, idx, Ansi.RESET,
                    line));
            Console.divide();
            Console.error("Item is already marked as done");
        } else {
            Console.error(String.format("Could not find index (%s)", idx));
        }
        return line;
    }
}
