package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.*;
import org.pmw.tinylog.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

@Command(name = "pri", description = "Prioritize a todo item")
public class TodoPriority implements Runnable {
    @ParentCommand
    private Todo todo;

    @Parameters(description = "Priority to assign. Valid values: ${COMPLETION-CANDIDATES}", arity = "0..1", index = "1")
    private Priority priority;

    @Option(names = {"-r", "--remove"}, description = "Remove priority from index")
    private boolean remove;

    @Parameters(description = "Index to prioritize", arity = "0..1", index = "0")
    private int idx;

    @Option(names = "--clean", description = "Remove all priorities")
    private boolean clean;

    @Inject
    private TodoReader reader;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        if (clean) {
            try (Scanner scanner = new Scanner(todo.getTodoFile())) {
                Console.info("Removing priority from all items");
                int currIdx = 0;
                while (scanner.hasNext()) {
                    String raw = scanner.nextLine();
                    TodoItem item = new TodoItem(raw);
                    if (item.isNotDone()) {
                        currIdx++;
                    }
                    if (item.hasPriority()) {
                        String newValue = item.rawValue().replaceFirst("^\\([A-Z]\\)\\s", "");
                        replace(item.rawValue(), newValue, todo.getTodoFile());
                        Console.info(String.format("[%s%2s%s] %s", Ansi.BLUE, currIdx, Ansi.RESET, new TodoItem(newValue)));
                    }
                }
            } catch (IOException e) {
                Console.error(String.format("Could not read file %s", todo.getTodoFile()));
            }
        } else if (idx == 0) {
            CommandLine.usage(TodoPriority.class, System.out);
        } else {
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
                            priority = reader.readHighestFreePriority(todo.getTodoFile());
                        }
                        newLine = String.format("(%s) %s", priority.name(), line.get().rawValue());
                    }
                }
                if (newLine != null) {
                    replace(line.get().rawValue(), newLine, todo.getTodoFile());

                    Console.info(String.format("[%s%s%s]: %s", Ansi.BLUE, idx, Ansi.RESET, line.get()));
                    Console.divide();
                    Console.info(String.format("[%s%s%s]: %s", Ansi.BLUE, idx, Ansi.RESET, new TodoItem(newLine)));
                }
            } else {
                Console.error(String.format("Could not find index (%s)", idx));
            }
        }
    }

    private void replace(final String line, final String newLine, final Path fromFile) {
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

        private static final HashMap<Priority, Priority> PROMOTE = new HashMap<>();

        static {
            PROMOTE.put(Priority.A, Priority.A);
            PROMOTE.put(Priority.B, Priority.A);
            PROMOTE.put(Priority.C, Priority.B);
            PROMOTE.put(Priority.D, Priority.C);
            PROMOTE.put(Priority.E, Priority.D);
            PROMOTE.put(Priority.F, Priority.E);
            PROMOTE.put(Priority.G, Priority.F);
            PROMOTE.put(Priority.H, Priority.G);
            PROMOTE.put(Priority.I, Priority.H);
            PROMOTE.put(Priority.J, Priority.I);
            PROMOTE.put(Priority.K, Priority.J);
            PROMOTE.put(Priority.L, Priority.K);
            PROMOTE.put(Priority.M, Priority.L);
            PROMOTE.put(Priority.N, Priority.M);
            PROMOTE.put(Priority.O, Priority.N);
            PROMOTE.put(Priority.P, Priority.O);
            PROMOTE.put(Priority.Q, Priority.P);
            PROMOTE.put(Priority.R, Priority.Q);
            PROMOTE.put(Priority.S, Priority.R);
            PROMOTE.put(Priority.T, Priority.S);
            PROMOTE.put(Priority.U, Priority.T);
            PROMOTE.put(Priority.V, Priority.U);
            PROMOTE.put(Priority.W, Priority.V);
            PROMOTE.put(Priority.X, Priority.W);
            PROMOTE.put(Priority.Y, Priority.X);
            PROMOTE.put(Priority.Z, Priority.Y);
        }

        Priority promote() {
            return PROMOTE.get(this);
        }
    }
}
