package net.avdw.todo;

import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TodoDirectory {
    private Path path;
    private List<TodoItem> todoItemList = new ArrayList<>();

    public TodoDirectory(final Path path) {
        this.path = path;
        todoItemList.addAll(loadItems());
    }

    /**
     * Filter the amount of todo items that are not complete in the path.
     * @return the number of incomplete items in the todo.txt file
     */
    public long numIncompleteItems() {
        return todoItemList.stream().filter(TodoItem::isNotDone).count();
    }

    private List<TodoItem> loadItems() {
        List<TodoItem> items = new ArrayList<>();
        try (Scanner scanner = new Scanner(path.resolve("todo.txt"))) {
            while (scanner.hasNext()) {
                items.add(new TodoItem(scanner.nextLine()));
            }
        } catch (IOException e) {
            Console.error(String.format("Could not load items from %s", path));
            Logger.error(e);
        }

        Logger.debug(String.format("Loaded items (%s)", items.size()));
        return items;
    }
}
