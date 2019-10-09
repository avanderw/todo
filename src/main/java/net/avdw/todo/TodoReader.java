package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.action.TodoPriority;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class TodoReader {

    private Todo todo;

    @Inject
    public TodoReader(final Todo todo) {
        this.todo = todo;
    }

    /**
     * Find the todo item in the todo.txt file.
     * Take into account the visibility of items configured when determining the index.
     * Currently this method does not take into account filters.
     *
     * @param todoFile the file to search through
     * @param idx      the index in the file to find
     * @return the todo item that was found
     */
    public Optional<TodoItemV1> readLine(final Path todoFile, final int idx) {
        TodoItemV1 readLine = null;
        try (Scanner scanner = new Scanner(todoFile)) {
            int lineNum = 0;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                TodoItemV1 item = new TodoItemV1(line);
                if (item.isNotDone() || todo.showAll()) {
                    lineNum++;
                    if (lineNum == idx) {
                        readLine = item;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            Logger.error(String.format("Error reading `%s`", todoFile));
            Logger.debug(e);
        }

        if (readLine != null) {
            return Optional.of(readLine);
        } else {
            return Optional.empty();
        }
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
        try (Scanner scanner = new Scanner(todoFile)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                TodoItemV1 item = new TodoItemV1(line);
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
