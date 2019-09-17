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
    public Optional<TodoItem> readLine(final Path todoFile, final int idx) {
        TodoItem readLine = null;
        try (Scanner scanner = new Scanner(todoFile)) {
            int lineNum = 0;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                TodoItem item = new TodoItem(line);
                if (item.isNotDone() || todo.showAll()) {
                    lineNum++;
                    if (lineNum == idx) {
                        readLine = item;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            Console.error(String.format("Error reading `%s`", todoFile));
            Logger.error(e);
        }

        if (readLine != null) {
            return Optional.of(readLine);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Find out the highest available priority that can be assigned that is not currently assigned.
     * @param todoFile the file to search for the priority in
     * @return the first available priority or the lowest priority if there is none
     */
    public TodoPriority.Priority readHighestFreePriority(final Path todoFile) {
        List<TodoPriority.Priority> priorities = new ArrayList<>(Arrays.asList(TodoPriority.Priority.values()));
        try (Scanner scanner = new Scanner(todoFile)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                TodoItem item = new TodoItem(line);
                if (item.hasPriority()) {
                    item.getPriority().ifPresent(priorities::remove);
                }
            }
        } catch (IOException e) {
            Console.error(String.format("Error reading `%s`", todoFile));
            Logger.debug(e);
        }

        if (priorities.isEmpty()) {
            return TodoPriority.Priority.Z;
        } else {
            return priorities.get(0);
        }
    }
}
