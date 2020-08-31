package net.avdw.todo.file;

import com.google.inject.Inject;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.TodoItemFactory;
import org.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
@Deprecated
public class TodoFileReader {

    private TodoItemFactory todoItemFactory;

    @Inject
    TodoFileReader(final TodoItemFactory todoItemFactory) {
        this.todoItemFactory = todoItemFactory;
    }

    /**
     * Read all the lines in the todo.txt file and convert them to todo items.
     *
     * @param todoFile the file to read todo line items from
     * @return a list of todo item objects
     */
    public List<TodoItem> readAll(final Path todoFile) {
        Logger.debug(String.format("Reading all todo.txt items from '%s'", todoFile));
        List<TodoItem> todoItemList = new ArrayList<>();
        try (Scanner scanner = new Scanner(todoFile)) {
            while (scanner.hasNextLine()) {
                todoItemList.add(todoItemFactory.create(todoItemList.size() + 1, scanner.nextLine()));
            }
        } catch (IOException e) {
            Logger.error(String.format("Could not read '%s' because %s", todoFile, e.getMessage()));
            Logger.debug(e);
        }
        return todoItemList;
    }
}
