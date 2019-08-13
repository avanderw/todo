package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.*;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;

@Command(name = "pri", description = "Prioritize a todo item")
public class TodoPriority implements Runnable {
    @ParentCommand
    private Todo todo;

    @Parameters(description = "Index to prioritize", arity = "1", index = "0")
    private int idx;

    @Parameters(description = "Priority to assign. Valid values: ${COMPLETION-CANDIDATES}", arity = "0..1", index = "1")
    private Priority priority;

    @Option(names = {"-r", "--remove"})
    private boolean remove;

    @Inject
    private TodoReader reader;

    @Override
    public void run() {
        Optional<TodoItem> line = reader.readLine(todo.getTodoFile(), idx);

        if (line.isPresent()) {
            String newLine = null;
            if (remove) {
                newLine = line.get().rawValue().replaceFirst("\\([A-Z]\\)\\s", "");
            } else if (line.get().isDone()) {
                Console.error("Priority cannot be assigned to complete items");
            } else {
                if (line.get().hasPriority()) {
                    if (priority == null) {
                        priority = line.get().getPriority().orElse(Priority.A);
                        priority = priority.promote();
                    }
                    newLine = line.get().rawValue().replaceFirst("\\([A-Z]\\)", String.format("(%s)", priority.name()));
                } else {
                    if (priority == null) {
                        priority = Priority.A;
                    }
                    newLine = String.format("(%s) %s", priority.name(), line.get().rawValue());
                }
            }
            if (newLine != null) {
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

    public enum Priority {
        A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z;

        private static final HashMap<Priority, Priority> promote = new HashMap<>();
        static {
            promote.put(Priority.A, Priority.A);
            promote.put(Priority.B, Priority.A);
            promote.put(Priority.C, Priority.B);
            promote.put(Priority.D, Priority.C);
            promote.put(Priority.E, Priority.D);
            promote.put(Priority.F, Priority.E);
            promote.put(Priority.G, Priority.F);
            promote.put(Priority.H, Priority.G);
            promote.put(Priority.I, Priority.H);
            promote.put(Priority.J, Priority.I);
            promote.put(Priority.K, Priority.J);
            promote.put(Priority.L, Priority.K);
            promote.put(Priority.M, Priority.L);
            promote.put(Priority.N, Priority.M);
            promote.put(Priority.O, Priority.N);
            promote.put(Priority.P, Priority.O);
            promote.put(Priority.Q, Priority.P);
            promote.put(Priority.R, Priority.Q);
            promote.put(Priority.S, Priority.R);
            promote.put(Priority.T, Priority.S);
            promote.put(Priority.U, Priority.T);
            promote.put(Priority.V, Priority.U);
            promote.put(Priority.W, Priority.V);
            promote.put(Priority.X, Priority.W);
            promote.put(Priority.Y, Priority.X);
            promote.put(Priority.Z, Priority.Y);
        }
        Priority promote() {
            return promote.get(this);
        }
    }
}
