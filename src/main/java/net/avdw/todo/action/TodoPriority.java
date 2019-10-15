package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.TodoReader;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.TodoItemFactory;
import net.avdw.todo.theme.ThemeApplicator;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Command(name = "pri", description = "Prioritize a todo item")
public class TodoPriority implements Runnable {
    @ParentCommand
    private Todo todo;

    @Parameters(description = "Index to prioritize", arity = "0..1", index = "0")
    private int idx = -1;
    @Parameters(description = "Priority to assign. Valid values: ${COMPLETION-CANDIDATES}", arity = "0..1", index = "1")
    private Priority priority;

    @Option(names = {"-r", "--remove"}, description = "Remove priority from index")
    private boolean remove;
    @Option(names = "--clear", description = "Remove all priorities")
    private boolean clear;
    @Option(names = "--optimize", description = "Optimize priority usage")
    private boolean optimize;
    @Option(names = "--shift-up", description = "Shift all the priorities one up")
    private boolean shiftUp;
    @Option(names = "--shift-down", description = "Shift all teh priorities one down")
    private boolean shiftDown;

    @Inject
    private TodoReader reader;
    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    private TodoFileWriter todoFileWriter;
    @Inject
    private TodoItemFactory todoItemFactory;
    @Inject
    private TodoList todoList;
    @Inject
    private ThemeApplicator themeApplicator;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        System.out.println(themeApplicator.h1("todo:priority"));
        if (shiftUp) {
            shiftUpPriorities(todo.getTodoFile());
            todoList.listPriorities(todo.getTodoFile());
        } else if (shiftDown) {
            shiftDownPriorities(todo.getTodoFile());
            todoList.listPriorities(todo.getTodoFile());
        } else if (optimize) {
            optimizePriorities(todo.getTodoFile());
            todoList.listPriorities(todo.getTodoFile());
        } else if (clear) {
            clearPriorities();
        } else if (idx == -1) {
            todoList.listPriorities(todo.getTodoFile());
        } else {
            List<TodoItem> allTodoItems = todoFileReader.readAll(todo.getTodoFile());
            if (idx > allTodoItems.size()) {
                Logger.warn(String.format("There are only '%s' items in the todo file and idx '%s' is too high", allTodoItems.size(), idx));
                return;
            } else if (idx <= 0) {
                Logger.warn(String.format("The idx '%s' cannot be negative", idx));
                return;
            }

            TodoItem todoItem = allTodoItems.get(idx - 1);
            String newLine = null;
            if (remove) {
                newLine = todoItem.rawValue().replaceFirst("\\([A-Z]\\)\\s", "");
            } else if (todoItem.isComplete()) {
                Logger.warn("Priority cannot be assigned to complete items");
            } else {
                if (todoItem.hasPriority()) {
                    if (priority == null) {
                        priority = todoItem.getPriority().orElse(Priority.A);
                        priority = priority.promote();
                    }
                    newLine = todoItem.rawValue().replaceFirst("\\([A-Z]\\)", String.format("(%s)", priority.name()));
                } else {
                    if (priority == null) {
                        priority = reader.readHighestFreePriority(todo.getTodoFile());
                    }
                    newLine = String.format("(%s) %s", priority.name(), todoItem.rawValue());
                }
            }

            if (newLine != null) {
                replace(todoItem.rawValue(), newLine, todo.getTodoFile());

                Logger.info(String.format("%s", todoItem));
                System.out.println(themeApplicator.hr());
                Logger.info(String.format("%s", todoItemFactory.create(todoItem.getIdx(), newLine)));
            }
        }
    }

    private void clearPriorities() {
        Logger.info("Removing priority from all items");
        List<TodoItem> allTodoItemList = todoFileReader.readAll(todo.getTodoFile());
        List<TodoItem> clearedTodoItemList = allTodoItemList.stream().map(todoItem -> {
            if (todoItem.hasPriority()) {
                TodoItem clearedTodoItem = todoItemFactory.create(todoItem.getIdx(),
                        todoItem.rawValue().replaceFirst("^\\([A-Z]\\)\\s", ""));
                Logger.info(String.format("%s", clearedTodoItem));
                return clearedTodoItem;
            } else {
                return todoItem;
            }
        }).collect(Collectors.toList());

        todoFileWriter.write(clearedTodoItemList, todo.getTodoFile());
    }

    private void optimizePriorities(final Path todoFile) {
        List<Priority> availablePriorities = new ArrayList<>(Arrays.asList(Priority.values()));
        List<Priority> usedPriorities = new ArrayList<>();

        List<TodoItem> allTodoItems = todoFileReader.readAll(todoFile);
        allTodoItems.forEach(todoItem -> todoItem.getPriority().ifPresent(priority -> {
            if (availablePriorities.contains(priority)) {
                usedPriorities.add(priority);
                availablePriorities.remove(priority);
            }
        }));

        Map<Priority, Priority> mapping = new HashMap<>();
        usedPriorities.forEach(priority -> {
            if (!availablePriorities.isEmpty()) {
                if (priority.compareTo(availablePriorities.get(0)) > 0) {
                    mapping.put(priority, availablePriorities.get(0));
                    availablePriorities.add(priority);
                    availablePriorities.remove(0);
                    availablePriorities.sort(Enum::compareTo);
                }
            } else {
                Logger.debug("No more priorities available to assign from");
            }
        });

        List<TodoItem> optimizedTodoItems = allTodoItems.stream().map(todoItem ->
                todoItem.getPriority().isPresent() && mapping.containsKey(todoItem.getPriority().get())
                        ? todoItemFactory.create(todoItem.getIdx(), todoItem.rawValue().replace(
                        String.format("(%s)", todoItem.getPriority().get()),
                        String.format("(%s)", mapping.get(todoItem.getPriority().get()))))
                        : todoItem)
                .collect(Collectors.toList());

        Logger.debug(String.format("Available priorities: %s", availablePriorities));
        Logger.debug(String.format("Used priorities: %s", usedPriorities));
        Logger.debug(String.format("Mapping: %s", mapping));
        if (mapping.isEmpty()) {
            Logger.info("The priorities are already optimized");
        } else {
            todoFileWriter.write(optimizedTodoItems, todo.getTodoFile());
            Logger.info("The priorities have been optimized");
        }
    }

    private void shiftDownPriorities(final Path todoFile) {
        // todo refactor to not read the file twice
        Optional<Priority> lowestFreePriority = reader.readLowestFreePriority(todoFile);
        if (lowestFreePriority.isPresent()) {
            if (lowestFreePriority.get() == Priority.Z) {
                List<TodoItem> allTodoItems = todoFileReader.readAll(todoFile);
                List<TodoItem> shiftedTodoItems = allTodoItems.stream().map(todoItem -> todoItem.getPriority().isPresent()
                        ? todoItemFactory.create(todoItem.getIdx(), todoItem.rawValue()
                        .replace(String.format("(%s)", todoItem.getPriority().get()), String.format("(%s)", todoItem.getPriority().get().demote())))
                        : todoItem)
                        .collect(Collectors.toList());

                todoFileWriter.write(shiftedTodoItems, todoFile);
                Logger.info("Priorities shifted down one");
            } else {
                Logger.warn("There is an item with a priority of Z");
                Logger.info("Cannot shift the priorities down by one");
            }
        } else {
            Logger.warn("There are no available priorities");
            Logger.info("Cannot shift the priorities down by one");
        }
    }

    private void shiftUpPriorities(final Path todoFile) {
        // todo refactor to not read the file twice
        if (reader.readHighestFreePriority(todoFile) == Priority.A) {
            List<TodoItem> allTodoItems = todoFileReader.readAll(todoFile);
            List<TodoItem> shiftedTodoItems = allTodoItems.stream().map(todoItem -> todoItem.getPriority().isPresent()
                    ? todoItemFactory.create(todoItem.getIdx(), todoItem.rawValue()
                    .replace(String.format("(%s)", todoItem.getPriority().get()), String.format("(%s)", todoItem.getPriority().get().promote())))
                    : todoItem)
                    .collect(Collectors.toList());

            todoFileWriter.write(shiftedTodoItems, todoFile);
            Logger.info("Priorities shifted up one");
        } else {
            Logger.warn("There is an item with a priority of A");
            Logger.info("Cannot shift the priorities up by one");
        }
    }

    private void replace(final String line, final String newLine, final Path fromFile) {
        try {
            String contents = new String(Files.readAllBytes(fromFile));
            Files.write(fromFile, contents.replace(line, newLine).getBytes());
        } catch (IOException e) {
            Logger.error(String.format("Error writing `%s`", fromFile));
            Logger.debug(e);
        }
    }

    public enum Priority {
        A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z;

        private static final HashMap<Priority, Priority> PROMOTE = new HashMap<>();
        private static final HashMap<Priority, Priority> DEMOTE = new HashMap<>();

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

            DEMOTE.put(Priority.A, Priority.B);
            DEMOTE.put(Priority.B, Priority.C);
            DEMOTE.put(Priority.C, Priority.D);
            DEMOTE.put(Priority.D, Priority.E);
            DEMOTE.put(Priority.E, Priority.F);
            DEMOTE.put(Priority.F, Priority.G);
            DEMOTE.put(Priority.G, Priority.H);
            DEMOTE.put(Priority.H, Priority.I);
            DEMOTE.put(Priority.I, Priority.J);
            DEMOTE.put(Priority.J, Priority.K);
            DEMOTE.put(Priority.K, Priority.L);
            DEMOTE.put(Priority.L, Priority.M);
            DEMOTE.put(Priority.M, Priority.N);
            DEMOTE.put(Priority.N, Priority.O);
            DEMOTE.put(Priority.O, Priority.P);
            DEMOTE.put(Priority.P, Priority.Q);
            DEMOTE.put(Priority.Q, Priority.R);
            DEMOTE.put(Priority.R, Priority.S);
            DEMOTE.put(Priority.S, Priority.T);
            DEMOTE.put(Priority.T, Priority.U);
            DEMOTE.put(Priority.U, Priority.V);
            DEMOTE.put(Priority.V, Priority.W);
            DEMOTE.put(Priority.W, Priority.X);
            DEMOTE.put(Priority.X, Priority.Y);
            DEMOTE.put(Priority.Y, Priority.Z);
            DEMOTE.put(Priority.Z, Priority.Z);
        }

        Priority promote() {
            return PROMOTE.get(this);
        }

        Priority demote() {
            return DEMOTE.get(this);
        }
    }
}
