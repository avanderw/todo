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

@Command(name = "start", description = "Start a todo item")
public class TodoStart implements Runnable {

    @ParentCommand
    private Todo todo;

    @Parameters(description = "Index to start", arity = "1")
    private int idx;

    @Inject
    TodoReader reader;

    @Override
    public void run() {
        Optional<TodoItem> line = reader.readLine(todo.getTodoFile(), idx);
        if (line.isPresent() && line.get().isNotDone()) {
            try {
                if (line.get().isStarted()) {
                    Console.info(String.format("[%s%s%s] %s",
                            Ansi.Blue, idx, Ansi.Reset,
                            line.get().rawValue()));
                    Console.divide();
                    Console.error("Item is already started");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String startedLine = String.format("%s start:%s", line.get().rawValue(), sdf.format(new Date()));
                    String contents = new String(Files.readAllBytes(todo.getTodoFile()));
                    Files.write(todo.getTodoFile(), contents.replace(line.get().rawValue(), startedLine).getBytes());

                    Console.info(String.format("[%s%s%s]: %s", Ansi.Blue, idx, Ansi.Reset, line.get()));
                    Console.divide();
                    Console.info(String.format("[%s%s%s]: %s", Ansi.Blue, idx, Ansi.Reset, new TodoItem(startedLine)));
                }
            } catch (IOException e) {
                Console.error(String.format("Error writing `%s`", todo.getTodoFile()));
                Logger.error(e);
            }
        } else if (line.isPresent() && line.get().isDone()) {
            Console.info(String.format("[%s%s%s] %s",
                    Ansi.Blue, idx, Ansi.Reset,
                    line));
            Console.divide();
            Console.error("Item is already marked as done");
        } else {
            Console.error(String.format("Could not find index (%s)", idx));
        }
    }
}
