package net.avdw.todo.admin;

import net.avdw.todo.Console;
import net.avdw.todo.Todo;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Command(name = "backup", description = "Write todo.txt.bak")
public class TodoBackup implements Runnable {
    @ParentCommand
    private Todo todo;

    @Override
    public void run() {
        try {
            Files.copy(todo.getTodoFile(), todo.getBackupFile(), StandardCopyOption.REPLACE_EXISTING);
            Console.info("Wrote todo.txt.bak");
        } catch (IOException e) {
            Console.info("Error backing up todo.txt");
            Logger.error(e);
        }
    }
}
