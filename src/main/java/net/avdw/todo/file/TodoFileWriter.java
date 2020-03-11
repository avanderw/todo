package net.avdw.todo.file;

import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Files;

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
}
