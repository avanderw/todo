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

@Command(name = "pri", description = "Prioritize a todo item")
public class TodoPriority implements Runnable {
    @ParentCommand
    private Todo todo;

    @Parameters(description = "Index to prioritize", arity = "1", index = "0")
    private int idx;

    @Parameters(description = "Priority to assign. Valid values: ${COMPLETION-CANDIDATES}", arity = "1", index = "1")
    private Priority priority;

    @Inject
    private TodoReader reader;

    @Override
    public void run() {
        Optional<TodoItem> line = reader.readLine(todo.getTodoFile(), idx);

        if (line.isPresent()) {
            if (line.get().isDone()) {
                Console.error("Priority cannot be assigned to complete items");
            } else {
                String newLine;
                if (line.get().hasPriority()) {
                    newLine = line.get().rawValue().replaceFirst("\\([A-Z]\\)", String.format("(%s)", priority.name()));
                } else {
                    newLine = String.format("(%s) %s", priority.name(), line.get().rawValue());
                }

                replace(line.get().rawValue(), newLine, todo.getTodoFile());

                Console.info(String.format("[%s%s%s]: %s", Ansi.Blue, idx, Ansi.Reset, line.get()));
                Console.divide();
                Console.info(String.format("[%s%s%s]: %s", Ansi.Blue, idx, Ansi.Reset, new TodoItem(newLine)));
            }
        } else {
            Console.error(String.format("Could not find index (%s)", idx));
        }
    }

    private void replace(String line, String newLine, Path fromFile) {
        try {
            String contents = new String(Files.readAllBytes(fromFile));
            Files.write(fromFile, contents.replace(line, newLine).getBytes());
        } catch (IOException e) {
            Console.error(String.format("Error writing `%s`", fromFile));
            Logger.error(e);
        }
    }

    enum Priority {
        A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
    }
}
