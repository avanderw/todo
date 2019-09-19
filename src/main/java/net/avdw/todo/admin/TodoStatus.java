package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.*;
import net.avdw.todo.property.GlobalProperty;
import net.avdw.todo.property.PropertyModule;
import org.pmw.tinylog.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Command(name = "status", description = "Display repository information")
public class TodoStatus implements Runnable {

    @ParentCommand
    private Todo todo;

    @Inject
    @GlobalTodo
    private Path globalPath;

    @Inject
    @LocalTodo
    private Path localPath;

    @Inject
    @GlobalProperty
    private Properties properties;

    @Inject
    @GlobalProperty
    private Path propertyPath;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        Console.h1("Working Paths");
        Console.info(String.format("Local    : %s", Files.exists(localPath) ? localPath : "todo init"));
        Console.info(String.format("Global   : %s", Files.exists(globalPath) ? globalPath : "todo --global init"));
        Console.info(String.format("Selected : %s", todo.findDirectory()));
        Console.blank();
        Console.h1("Known Paths");
        if (properties.containsKey(PropertyModule.TODO_PATHS)) {
            String todoPaths = properties.getProperty(PropertyModule.TODO_PATHS);
            List<String> removePaths = new ArrayList<>();
            Arrays.stream(todoPaths.split(";")).forEach(path -> {
                try {
                    TodoDirectory todoDirectory = new TodoDirectory(Paths.get(path));
                    Console.info(String.format("[%s%2s%s] %s", Ansi.BLUE, todoDirectory.numIncompleteItems(), Ansi.RESET, path));
                } catch (TodoDirectory.ReadException e) {
                    Logger.info(String.format("Going to remove known path %s", path));
                    removePaths.add(path);
                }
            });

            if (!removePaths.isEmpty()) {
                for (String path : removePaths) {
                    todoPaths = todoPaths.replace(path, "");
                    todoPaths = todoPaths.replaceAll(";;", ";");
                }
                properties.setProperty(PropertyModule.TODO_PATHS, todoPaths);
                try {
                    properties.store(new FileWriter(propertyPath.toFile()), "Todo Properties");
                } catch (IOException e) {
                    Logger.error(e.getMessage());
                    Logger.debug(e);
                }
                Logger.info("Wrote new property file");
            }
        } else {
            Console.info("No paths found");
        }

        if (!Files.exists(localPath) || !Files.exists(globalPath)) {
            Console.divide();
            CommandLine.usage(TodoInit.class, System.out);
        }
    }
}
