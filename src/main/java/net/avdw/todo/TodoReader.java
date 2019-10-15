package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.action.TodoPriority;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.TodoItemFactory;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class TodoReader {

    private TodoItemFactory todoItemFactory;

    @Inject
    public TodoReader(final TodoItemFactory todoItemFactory) {
        this.todoItemFactory = todoItemFactory;
    }

    /**
     * Find out the highest available priority that can be assigned that is not currently assigned.
     *
     * @param todoFile the file to search for the priority in
     * @return the first available priority or the lowest priority if there is none
     */
    public TodoPriority.Priority readHighestFreePriority(final Path todoFile) {
        List<TodoPriority.Priority> priorities = getAvailablePriorities(todoFile);

        if (priorities.isEmpty()) {
            return TodoPriority.Priority.Z;
        } else {
            return priorities.get(0);
        }
    }

    /**
     * Find out the lowest available priority that can be assigned that is not currently assigned.
     *
     * @param todoFile the file to search for the priority in
     * @return the last available priority or empty if there is none
     */
    public Optional<TodoPriority.Priority> readLowestFreePriority(final Path todoFile) {
        List<TodoPriority.Priority> priorities = getAvailablePriorities(todoFile);

        if (priorities.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(priorities.get(priorities.size() - 1));
        }
    }

    private List<TodoPriority.Priority> getAvailablePriorities(final Path todoFile) {
        List<TodoPriority.Priority> priorities = new ArrayList<>(Arrays.asList(TodoPriority.Priority.values()));
        int count = 0;
        try (Scanner scanner = new Scanner(todoFile)) {
            while (scanner.hasNext()) {
                count++;
                String line = scanner.nextLine();
                TodoItem item = todoItemFactory.create(count, line);
                if (item.hasPriority()) {
                    item.getPriority().ifPresent(priorities::remove);
                }
            }
        } catch (IOException e) {
            Logger.error(String.format("Error reading `%s`", todoFile));
            Logger.debug(e);
        }
        return priorities;
    }
}
