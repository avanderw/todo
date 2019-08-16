package net.avdw.todo.admin;

import net.avdw.todo.Console;
import net.avdw.todo.Todo;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@Command(name = "clear", description = "Clear the todo.txt file")
public class TodoClear implements Runnable {
    @ParentCommand
    private Todo todo;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        todo.backup();
        try {
            Files.write(todo.getTodoFile(), "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Console.info(String.format("Cleared file: %s", todo.getTodoFile()));
    }
}
