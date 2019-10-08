package net.avdw.todo;

import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TodoDirectory {
    private Path path;
    private List<TodoItemV1> todoItemList = new ArrayList<>();

    public TodoDirectory(final Path path) throws ReadException {
        this.path = path;
        todoItemList.addAll(loadItems());
    }

    /**
     * Filter the amount of todo items that are not complete in the path.
     * @return the number of incomplete items in the todo.txt file
     */
    public long numIncompleteItems() {
        return todoItemList.stream().filter(TodoItemV1::isNotDone).count();
    }

    private List<TodoItemV1> loadItems() throws ReadException {
        List<TodoItemV1> items = new ArrayList<>();
        try (Scanner scanner = new Scanner(path.resolve("todo.txt"))) {
            while (scanner.hasNext()) {
                items.add(new TodoItemV1(scanner.nextLine()));
            }
        } catch (IOException e) {
            Logger.warn(String.format("Could not load items from %s", path));
            Logger.debug(e);
            throw new ReadException(e);
        }

        Logger.debug(String.format("Loaded items (%s)", items.size()));
        return items;
    }

    public static class ReadException extends Exception {
        ReadException(final Throwable cause) {
            super(cause);
        }
    }
}
