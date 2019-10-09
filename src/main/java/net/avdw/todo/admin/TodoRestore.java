package net.avdw.todo.admin;

import net.avdw.todo.Todo;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static net.avdw.todo.render.ConsoleFormatting.h1;

@Command(name = "restore", description = "Replace todo.txt with backup")
public class TodoRestore implements Runnable {
    @ParentCommand
    private Todo todo;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        h1("todo:restore");
        try {
            Files.copy(todo.getBackupFile(), todo.getTodoFile(), StandardCopyOption.REPLACE_EXISTING);
            Logger.info(String.format("Replaced `%s` with `%s`", todo.getTodoFile(), todo.getBackupFile()));
        } catch (IOException e) {
            Logger.error("Error restoring todo.txt");
            Logger.debug(e);
        }
    }
}
