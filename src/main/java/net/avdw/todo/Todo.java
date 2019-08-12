package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.action.*;
import net.avdw.todo.admin.*;
import net.avdw.todo.config.PropertyModule;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@Command(name = "todo",
        description = "The procrastination tool",
        version = "1.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                HelpCommand.class,
                TodoInit.class,
                TodoSet.class,
                TodoStatus.class,
                TodoBackup.class,
                TodoRestore.class,
                TodoSort.class,
                TodoEdit.class,
                TodoMigrate.class,
                TodoRename.class,
                TodoClear.class,
                TodoList.class,
                TodoAdd.class,
                TodoStart.class,
                TodoPriority.class,
                TodoDone.class,
                TodoRemove.class
        })
public class Todo implements Runnable {
    @Option(names = {"-g", "--global"}, description = "Use the global directory")
    public boolean global;

    @Option(names = {"-a", "--all"}, description = "Show completed items")
    private boolean showAll;

    @Option(names = {"-b", "--backup"}, description = "Backup before command is executed")
    private boolean backup;

    @Inject
    @Global
    public Path globalPath;

    @Inject
    @Local
    public Path localPath;

    @Inject
    Properties properties;

    @Inject
    @Property
    Path propertyPath;

    public void run() {
        Path directory = getDirectory();

        if (Files.exists(directory)) {
            Console.info(String.format("Directory: %s", directory));
            CommandLine.usage(Todo.class, System.out);
        } else {
            Console.error("No directory found (or any of the parent directories)");
            CommandLine.usage(TodoInit.class, System.out);
        }
    }

    public Path getDirectory() {
        if (Files.exists(localPath) && Files.exists(globalPath)) {
            return global ? globalPath : localPath;
        } else if (Files.exists(localPath)) {
            return localPath;
        } else {
            return globalPath;
        }
    }

    public Path getTodoFile() {
        Path directory = getDirectory();
        if (Files.exists(directory)) {
            Set<String> paths;
            if (properties.containsKey(PropertyModule.TODO_PATHS)) {
                paths = Arrays.stream(properties.getProperty(PropertyModule.TODO_PATHS).split(";")).collect(Collectors.toSet());
            } else {
                paths = new HashSet<>();
            }
            paths.add(directory.toAbsolutePath().toString());

            try {
                properties.setProperty(PropertyModule.TODO_PATHS, String.join(";", paths));
                properties.store(new FileWriter(propertyPath.toFile()), "Todo Properties");
            } catch (IOException e) {
                Console.error("Could not save property file");
            }
        }
        return directory.resolve("todo.txt");
    }

    public Path getBackupFile() {
        return getDirectory().resolve("todo.txt.bak");
    }

    public boolean showAll() {
        return showAll;
    }

    public void backup() {
        if (backup) {
            TodoBackup todoBackup = new TodoBackup();
            todoBackup.backup(getTodoFile(), getBackupFile());
        }
    }
}
