package net.avdw.todo.file;

import net.avdw.todo.item.TodoItem;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TodoFileWriter {
    /**
     * Rewrite a list of todo items into the specified file.
     *
     * @param todoFile the file to write the todo items to
     */
    public void write(final TodoFile todoFile) {
        StringBuilder stringBuilder = new StringBuilder();
        todoFile.getTodoItemList().getAll().forEach(todoItem -> stringBuilder.append(String.format("%s%n", todoItem.getRawValue())));
        try {
            Files.write(todoFile.getPath(), stringBuilder.toString().getBytes());
            Logger.debug(String.format("Wrote list of todo items to file '%s'", todoFile.getPath().toAbsolutePath()));
        } catch (IOException e) {
            Logger.error(String.format("Error writing `%s`, because %s", todoFile, e.getMessage()));
            Logger.debug(e);
        }
    }

    public void write(final Path filePath, final List<TodoItem> todoItemList) {
        StringBuilder stringBuilder = new StringBuilder();
        todoItemList.stream()
                .map(todoItem -> String.format("%s%n", todoItem.getRawValue()))
                .forEach(stringBuilder::append);
        try {
            Files.write(filePath, stringBuilder.toString().getBytes());
            Logger.debug("Wrote path {}", filePath);
        } catch (IOException e) {
            Logger.error(e.getMessage());
            Logger.debug(e);
        }
    }
}
