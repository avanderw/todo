package net.avdw.todo;

import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TodoWriter {
    /**
     * Rewrite a list of todo items into the specified file.
     *
     * @param todoItems the list of todo items
     * @param todoFile  the file to write the todo items to
     */
    public void write(final List<TodoItem> todoItems, final Path todoFile) {
        StringBuilder stringBuilder = new StringBuilder();
        todoItems.forEach(todoItem -> stringBuilder.append(String.format("%s%n", todoItem.rawValue())));
        try {
            Files.write(todoFile, stringBuilder.toString().getBytes());
            Logger.debug(String.format("Wrote list of todo items to file '%s'", todoFile));
        } catch (IOException e) {
            Console.error(String.format("Error writing `%s`", todoFile));
            Logger.debug(e);
        }
    }
}
