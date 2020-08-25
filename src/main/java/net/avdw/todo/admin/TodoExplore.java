package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.TodoCli;
import net.avdw.todo.theme.ThemeApplicator;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Command(name = "explore", description = "Open the .todo directory")
public class TodoExplore implements Runnable {
    @ParentCommand
    private TodoCli todoCli;

    @Inject
    private ThemeApplicator themeApplicator;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        System.out.println(themeApplicator.header("todo:explore"));
        Path directory = todoCli.resolveTodoPath();

        if (Files.exists(directory)) {
            Logger.info(String.format("Exploring %s", directory));
            ProcessBuilder pb = new ProcessBuilder("explorer.exe", directory.toString());
            try {
                pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
