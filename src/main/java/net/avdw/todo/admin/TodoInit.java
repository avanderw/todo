package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.Console;
import net.avdw.todo.Global;
import net.avdw.todo.Local;
import net.avdw.todo.Todo;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Command(name = "init", description = "Initialize .todo directory")
public class TodoInit implements Runnable {
    @ParentCommand
    private Todo todo;

    @Inject
    @Global
    private Path globalPath;

    @Inject
    @Local
    private Path localPath;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        Path path = todo.isGlobal() ? globalPath : localPath;

        if (Files.exists(path)) {
            Console.error("Directory `%s` already exists");
        } else {
            try {
                Files.createDirectories(path);
                Files.createFile(path.resolve("todo.txt"));
                Console.info(String.format("Initialized `%s` with a blank todo.txt", path));
            } catch (IOException e) {
                Console.error(String.format("Could not initialize directory `%s`", path));
                Logger.error(e);
            }
        }
    }
}
