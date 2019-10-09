package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.GlobalTodo;
import net.avdw.todo.LocalTodo;
import net.avdw.todo.Todo;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.avdw.todo.render.ConsoleFormatting.h1;

@Command(name = "init", description = "Initialize .todo directory")
public class TodoInit implements Runnable {
    @ParentCommand
    private Todo todo;

    @Inject
    @GlobalTodo
    private Path globalPath;

    @Inject
    @LocalTodo
    private Path localPath;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        h1("todo:init");
        Path path = todo.isGlobal() ? globalPath : localPath;

        if (Files.exists(path)) {
            Logger.warn("Directory `%s` already exists");
        } else {
            try {
                Files.createDirectories(path);
                Files.createFile(path.resolve("todo.txt"));
                Logger.info(String.format("Initialized `%s` with a blank todo.txt", path));
            } catch (IOException e) {
                Logger.error(String.format("Could not initialize directory `%s`", path));
                Logger.debug(e);
            }
        }
    }
}
