package net.avdw.todo.admin;

import net.avdw.todo.Console;
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

    @Override
    public void run() {
        Path path = todo.global ? todo.globalPath : todo.localPath;

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
