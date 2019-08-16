package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.*;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
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

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        Optional<TodoItem> line = reader.readLine(todo.getTodoFile(), idx);
        if (line.isPresent() && line.get().isNotDone()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String completeLine = String.format("x %s %s",
                        sdf.format(new Date()),
                        line.get().rawValue().replaceFirst("\\([A-Z]\\) ", ""));

                String contents = new String(Files.readAllBytes(todo.getTodoFile()));
                Files.write(todo.getTodoFile(), contents.replace(line.get().rawValue(), completeLine).getBytes());

                Console.info(String.format("[%s%s%s]: %s", Ansi.BLUE, idx, Ansi.RESET, line.get()));
                Console.divide();
                Console.info(String.format("[%s%s%s]: %s", Ansi.BLUE, idx, Ansi.RESET, new TodoItem(completeLine)));
            } catch (IOException e) {
                Console.error(String.format("Error writing `%s`", todo.getTodoFile()));
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
    }
}
