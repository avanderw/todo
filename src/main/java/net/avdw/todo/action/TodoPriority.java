package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.MainCli;
import net.avdw.todo.file.TodoFile;
import net.avdw.todo.file.TodoFileFactory;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.TodoItemFactory;
import net.avdw.todo.item.TodoItemModifier;
import net.avdw.todo.item.list.TodoItemList;
import net.avdw.todo.item.list.TodoItemListQuery;
import net.avdw.todo.theme.Theme;
import net.avdw.todo.Priority;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @see net.avdw.todo.PriorityCli
 */
@Deprecated
@Command(name = "pri", description = "Prioritize a todo item")
public class TodoPriority implements Runnable {
    @ParentCommand
    private MainCli mainCli;

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
    private TodoFileWriter todoFileWriter;
    @Inject
    private TodoItemFactory todoItemFactory;
    @Inject
    private TodoFileFactory todoFileFactory;
    @Inject
    private TodoItemModifier todoItemModifier;
    @Inject
    private TodoItemListQuery todoItemListQuery;
    @Inject
    private Theme theme;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        TodoFile fileBefore = todoFileFactory.create(mainCli.getTodoFile());
        List<TodoItem> changedTodoItemList;
        if (shiftUp) {
            changedTodoItemList = shiftUpPriorities(fileBefore);
        } else if (shiftDown) {
            changedTodoItemList = shiftDownPriorities(fileBefore);
        } else if (optimize) {
            changedTodoItemList = optimizePriorities(fileBefore);
        } else if (clear) {
            changedTodoItemList = clearPriorities(fileBefore);
        } else if (idx == -1) {
            Logger.debug("No action, default to showing priorities");
            changedTodoItemList = fileBefore.getTodoItemList().getPriority();
        } else if (remove) {
            changedTodoItemList = removePriority(fileBefore, idx);
        } else {
            changedTodoItemList = changePriority(fileBefore, idx, priority);
        }

        theme.printHeader("priority");
        changedTodoItemList.forEach(theme::printFullTodoItemWithIdx);
        theme.printDuration();
        theme.printDisplaySummary(changedTodoItemList.size(), fileBefore.getTodoItemList().getAll().size());
    }

    private List<TodoItem> changePriority(final TodoFile fileBefore, final int index, final Priority priority) {
        TodoItem indexedTodoItem = fileBefore.getTodoItemList().getAll().get(index - 1);
        TodoItem changedTodoItem;
        if (indexedTodoItem.isComplete()) {
            Logger.warn("Priority cannot be assigned to complete items");
            changedTodoItem = indexedTodoItem;
        } else if (priority != null) {
            changedTodoItem = todoItemModifier.changePriority(indexedTodoItem, priority);
        } else if (indexedTodoItem.getPriority().isPresent()) {
            changedTodoItem = todoItemModifier.changePriority(indexedTodoItem, indexedTodoItem.getPriority().get().promote());
        } else {
            changedTodoItem = todoItemModifier.changePriority(indexedTodoItem, todoItemListQuery.queryHighestFreePriority(fileBefore).orElse(Priority.Z));
        }

        List<TodoItem> changedPriorityTodoItemList = new ArrayList<>();
        changedPriorityTodoItemList.add(changedTodoItem);

        List<TodoItem> fileAfterTodoItemList = new ArrayList<>(fileBefore.getTodoItemList().getAll());
        fileAfterTodoItemList.set(index - 1, changedTodoItem);
        todoFileWriter.write(new TodoFile(fileBefore.getPath(), fileAfterTodoItemList));
        return changedPriorityTodoItemList;
    }

    private List<TodoItem> removePriority(final TodoFile fileBefore, final int index) {
        TodoItem strippedPriority = todoItemModifier.stripPriority(fileBefore.getTodoItemList().getAll().get(index - 1));
        List<TodoItem> fileAfterTodoItemList = new ArrayList<>(fileBefore.getTodoItemList().getAll());
        fileAfterTodoItemList.set(index - 1, strippedPriority);
        todoFileWriter.write(new TodoFile(fileBefore.getPath(), fileAfterTodoItemList));
        List<TodoItem> changedTodoItemList = new ArrayList<>();
        changedTodoItemList.add(strippedPriority);
        return changedTodoItemList;
    }

    private List<TodoItem> clearPriorities(final TodoFile fileBefore) {
        List<TodoItem> fileAfterTodoItemList = fileBefore.getTodoItemList().getAll().stream().map(todoItem -> todoItemModifier.stripPriority(todoItem)).collect(Collectors.toList());
        todoFileWriter.write(new TodoFile(fileBefore.getPath(), fileAfterTodoItemList));
        return new TodoItemList(fileAfterTodoItemList).getPriority();
    }

    private List<TodoItem> optimizePriorities(final TodoFile fileBefore) {
        List<Priority> availablePriorities = new ArrayList<>(Arrays.asList(Priority.values()));
        List<Priority> usedPriorities = new ArrayList<>();

        fileBefore.getTodoItemList().getAll().forEach(todoItem -> todoItem.getPriority().ifPresent(priority -> {
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

        List<TodoItem> fileAfterTodoItemList = fileBefore.getTodoItemList().getAll().stream().map(todoItem ->
                todoItem.getPriority().isPresent() && mapping.containsKey(todoItem.getPriority().get())
                        ? todoItemFactory.create(todoItem.getIdx(), todoItem.getRawValue().replace(
                        String.format("(%s)", todoItem.getPriority().get()),
                        String.format("(%s)", mapping.get(todoItem.getPriority().get()))))
                        : todoItem)
                .collect(Collectors.toList());

        Logger.debug(String.format("Available priorities: %s", availablePriorities));
        Logger.debug(String.format("Used priorities: %s", usedPriorities));
        Logger.debug(String.format("Mapping: %s", mapping));
        if (mapping.isEmpty()) {
            Logger.info("The priorities are already optimized");
            return fileBefore.getTodoItemList().getPriority();
        } else {
            Logger.info("The priorities have been optimized");
            todoFileWriter.write(new TodoFile(fileBefore.getPath(), fileAfterTodoItemList));
            return new TodoItemList(fileAfterTodoItemList).getPriority();
        }
    }

    private List<TodoItem> shiftDownPriorities(final TodoFile fileBefore) {
        Optional<Priority> lowestFreePriority = todoItemListQuery.queryLowestFreePriority(fileBefore);
        if (lowestFreePriority.isPresent() && lowestFreePriority.get() == Priority.Z) {
            List<TodoItem> shiftedTodoItems = fileBefore.getTodoItemList().getAll().stream().map(todoItem -> todoItem.getPriority().isPresent()
                    ? todoItemFactory.create(todoItem.getIdx(), todoItem.getRawValue()
                    .replace(String.format("(%s)", todoItem.getPriority().get()), String.format("(%s)", todoItem.getPriority().get().demote())))
                    : todoItem)
                    .collect(Collectors.toList());

            TodoFile fileAfter = new TodoFile(fileBefore.getPath(), shiftedTodoItems);
            Logger.debug("Priorities shifted down one");
            todoFileWriter.write(fileAfter);
            return fileAfter.getTodoItemList().getPriority();
        } else {
            Logger.warn("Cannot shift the priorities down by one");
            return fileBefore.getTodoItemList().getPriority();
        }
    }

    private List<TodoItem> shiftUpPriorities(final TodoFile fileBefore) {
        Optional<Priority> highestFreePriority = todoItemListQuery.queryHighestFreePriority(fileBefore);
        if (highestFreePriority.isPresent() && !highestFreePriority.get().equals(Priority.A)) {
            List<TodoItem> shiftedTodoItems = fileBefore.getTodoItemList().getAll().stream().map(todoItem -> todoItem.getPriority().isPresent()
                    ? todoItemFactory.create(todoItem.getIdx(), todoItem.getRawValue()
                    .replace(String.format("(%s)", todoItem.getPriority().get()), String.format("(%s)", todoItem.getPriority().get().promote())))
                    : todoItem)
                    .collect(Collectors.toList());

            TodoFile fileAfter = new TodoFile(fileBefore.getPath(), shiftedTodoItems);
            Logger.debug("Priorities shifted up one");
            todoFileWriter.write(fileAfter);
            return fileAfter.getTodoItemList().getPriority();
        } else {
            Logger.warn("Cannot shift the priorities up by one");
            return fileBefore.getTodoItemList().getPriority();
        }
    }

}
