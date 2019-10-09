package net.avdw.todo.admin;

import net.avdw.todo.Todo;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static net.avdw.todo.render.ConsoleFormatting.h1;

@Command(name = "clear", description = "Clear the todo.txt file")
public class TodoClear implements Runnable {
    @ParentCommand
    private Todo todo;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        h1("todo:clear");
        todo.backup();
        try {
            Files.write(todo.getTodoFile(), "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info(String.format("Cleared file: %s", todo.getTodoFile()));
    }
}
