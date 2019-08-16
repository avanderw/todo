package net.avdw.todo.admin;

import net.avdw.todo.Console;
import net.avdw.todo.Todo;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Command(name = "restore", description = "Replace todo.txt with backup")
public class TodoRestore implements Runnable {
    @ParentCommand
    private Todo todo;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        try {
            Files.copy(todo.getBackupFile(), todo.getTodoFile(), StandardCopyOption.REPLACE_EXISTING);
            Console.info(String.format("Replaced `%s` with `%s`", todo.getTodoFile(), todo.getBackupFile()));
        } catch (IOException e) {
            Console.error("Error restoring todo.txt");
            Logger.error(e);
        }
    }
}
