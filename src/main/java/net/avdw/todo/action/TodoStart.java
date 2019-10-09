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

import static net.avdw.todo.render.ConsoleFormatting.h1;
import static net.avdw.todo.render.ConsoleFormatting.hr;

@Command(name = "start", description = "Start a todo item")
public class TodoStart implements Runnable {

    @ParentCommand
    private Todo todo;

    @Parameters(description = "Index to start", arity = "1")
    private int idx;

    @Inject
    private TodoReader reader;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        h1("todo:start");
        Optional<TodoItemV1> line = reader.readLine(todo.getTodoFile(), idx);
        if (line.isPresent() && line.get().isNotDone()) {
            try {
                if (line.get().isStarted()) {
                    Logger.info(String.format("[%s%s%s] %s",
                            Ansi.BLUE, idx, Ansi.RESET,
                            line.get().rawValue()));
                    hr();
                    Logger.warn("Item is already started");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String startedLine = String.format("%s start:%s", line.get().rawValue(), sdf.format(new Date()));
                    if (!line.get().hasPriority()) {
                        startedLine = String.format("(%s) %s", reader.readHighestFreePriority(todo.getTodoFile()).name(), startedLine);
                    }
                    String contents = new String(Files.readAllBytes(todo.getTodoFile()));
                    Files.write(todo.getTodoFile(), contents.replace(line.get().rawValue(), startedLine).getBytes());

                    Logger.info(String.format("[%s%s%s]: %s", Ansi.BLUE, idx, Ansi.RESET, line.get()));
                    hr();
                    Logger.info(String.format("[%s%s%s]: %s", Ansi.BLUE, idx, Ansi.RESET, new TodoItemV1(startedLine)));
                }
            } catch (IOException e) {
                Logger.error(String.format("Error writing `%s`", todo.getTodoFile()));
                Logger.debug(e);
            }
        } else if (line.isPresent() && line.get().isDone()) {
            Logger.info(String.format("[%s%s%s] %s",
                    Ansi.BLUE, idx, Ansi.RESET,
                    line));
            hr();
            Logger.warn("Item is already marked as done");
        } else {
            Logger.warn(String.format("Could not find index (%s)", idx));
        }
    }
}
