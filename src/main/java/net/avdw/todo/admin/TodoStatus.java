package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.*;
import net.avdw.todo.config.PropertyModule;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

@Command(name = "status", description = "Display repository information")
public class TodoStatus implements Runnable {

    @ParentCommand
    private Todo todo;

    @Inject
    @Global
    private Path globalPath;

    @Inject
    @Local
    private Path localPath;

    @Inject
    private Properties properties;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        Console.h1("Working Paths");
        Console.info(String.format("Local    : %s", Files.exists(localPath) ? localPath : "todo init"));
        Console.info(String.format("Global   : %s", Files.exists(globalPath) ? globalPath : "todo --global init"));
        Console.info(String.format("Selected : %s", todo.getDirectory()));
        Console.blank();
        Console.h1("Known Paths");
        if (properties.containsKey(PropertyModule.TODO_PATHS)) {
            Arrays.stream(properties.getProperty(PropertyModule.TODO_PATHS).split(";")).forEach(path -> {
                TodoDirectory todoDirectory = new TodoDirectory(Paths.get(path));
                Console.info(String.format("[%s%2s%s] %s", Ansi.BLUE, todoDirectory.numIncompleteItems(), Ansi.RESET, path));
            });
        } else {
            Console.info("No paths found");
        }

        if (!Files.exists(localPath) || !Files.exists(globalPath)) {
            Console.divide();
            CommandLine.usage(TodoInit.class, System.out);
        }
    }
}
