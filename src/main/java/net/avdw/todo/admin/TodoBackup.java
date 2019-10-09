package net.avdw.todo.admin;

import net.avdw.todo.Todo;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static net.avdw.todo.render.ConsoleFormatting.h1;

@Command(name = "backup", description = "Write todo.txt.bak")
public class TodoBackup implements Runnable {
    @ParentCommand
    private Todo todo;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        h1("todo:backup");
        backup(todo.getTodoFile(), todo.getBackupFile());
    }

    /**
     * Copy a file from one location to another.
     * @param from the file to backup
     * @param to the backed up file
     */
    public void backup(final Path from, final Path to) {
        try {
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            Logger.info(String.format("Replaced '%s' with '%s'", to, from));
        } catch (IOException e) {
            Logger.error(String.format("Error writing '%s'", to));
            Logger.debug(e);
        }
    }
}
