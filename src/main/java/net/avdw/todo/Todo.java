package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.action.*;
import net.avdw.todo.admin.*;
import net.avdw.todo.property.GlobalProperty;
import net.avdw.todo.property.PropertyModule;
import org.pmw.tinylog.Logger;
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
                TodoExplore.class,
                TodoMigrate.class,
                TodoRename.class,
                TodoClear.class,
                TodoList.class,
                TodoAdd.class,
                TodoStart.class,
                TodoPriority.class,
                TodoDone.class,
                TodoRemove.class,
                TodoRepeat.class
        })
public class Todo implements Runnable {
    @Option(names = {"-g", "--global"}, description = "Use the global directory")
    private boolean global;

    @Option(names = {"-a", "--all"}, description = "Show completed items")
    private boolean showAll;

    @Option(names = {"-b", "--backup"}, description = "Backup before command is executed")
    private boolean backup;

    @Inject
    @GlobalTodo
    private Path globalTodoPath;

    @Inject
    @LocalTodo
    private Path localTodoPath;

    @Inject
    @GlobalProperty
    private Properties globalProperties;

    @Inject
    @GlobalProperty
    private Path globalPropertyPath;

    /**
     * Entry point for picocli.
     */
    public void run() {
        Path directory = findDirectory();

        if (Files.exists(directory)) {
            Console.info(String.format("Directory: %s", directory));
            CommandLine.usage(Todo.class, System.out);
        } else {
            Console.error("No directory found (or any of the parent directories)");
            CommandLine.usage(TodoInit.class, System.out);
        }
    }

    /**
     * Return the local path unless overridden with the global flag.
     * @return the path that resolved
     */
    public Path findDirectory() {
        if (Files.exists(localTodoPath) && Files.exists(globalTodoPath)) {
            Logger.debug(String.format("Local todo exists: %s", localTodoPath));
            Logger.debug(String.format("Global todo exists: %s", globalTodoPath));
            return global ? globalTodoPath : localTodoPath;
        } else if (Files.exists(localTodoPath)) {
            Logger.debug(String.format("Local todo exists: %s", localTodoPath));
            return localTodoPath;
        } else {
            Logger.debug(String.format("Global todo exists: %s", globalTodoPath));
            return globalTodoPath;
        }
    }

    /**
     * Get the todo.txt file in the todo directory.
     * Update the properties to include the directory as a known path.
     *
     * @return the path to the todo.txt file
     */
    public Path getTodoFile() {
        Path directory = findDirectory();
        if (Files.exists(directory)) {
            Set<String> paths;
            if (globalProperties.containsKey(PropertyModule.TODO_PATHS)) {
                paths = Arrays.stream(globalProperties.getProperty(PropertyModule.TODO_PATHS).split(";")).collect(Collectors.toSet());
            } else {
                paths = new HashSet<>();
            }
            paths.add(directory.toAbsolutePath().toString());

            try {
                globalProperties.setProperty(PropertyModule.TODO_PATHS, String.join(";", paths));
                globalProperties.store(new FileWriter(globalPropertyPath.toFile()), "Todo Properties");
                Logger.debug(String.format("Wrote %s", globalPropertyPath));
            } catch (IOException e) {
                Console.error("Could not save property file");
            }
        }
        return directory.resolve("todo.txt");
    }

    /**
     * Wrapper to protect the naming of the backup file.
     * @return the path to the backup file
     */
    public Path getBackupFile() {
        return findDirectory().resolve("todo.txt.bak");
    }

    /**
     * Whether complete todos should be shown.
     * @return whether to include complete todos
     */
    public boolean showAll() {
        return showAll;
    }

    /**
     * Wrapper to backup if the flag is set.
     * The intention is for this to be used with state changing sub-commands.
     */
    public void backup() {
        if (backup) {
            TodoBackup todoBackup = new TodoBackup();
            todoBackup.backup(getTodoFile(), getBackupFile());
        }
    }

    /**
     * Is the global todo path targeted.
     * @return whether the global flag was set
     */
    public boolean isGlobal() {
        return global;
    }
}
