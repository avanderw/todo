package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.theme.ThemeApplicator;
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

    @Inject
    private ThemeApplicator themeApplicator;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        System.out.println(themeApplicator.header("todo:restore"));
        try {
            Files.copy(todo.getBackupFile(), todo.getTodoFile(), StandardCopyOption.REPLACE_EXISTING);
            Logger.info(String.format("Replaced `%s` with `%s`", todo.getTodoFile(), todo.getBackupFile()));
        } catch (IOException e) {
            Logger.error("Error restoring todo.txt");
            Logger.debug(e);
        }
    }
}
