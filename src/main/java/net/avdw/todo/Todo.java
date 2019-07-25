package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.action.TodoAdd;
import net.avdw.todo.action.TodoDone;
import net.avdw.todo.action.TodoList;
import net.avdw.todo.action.TodoRemove;
import net.avdw.todo.admin.*;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;

import java.nio.file.Files;
import java.nio.file.Path;

@Command(name = "todo",
        description = "The procrastination tool",
        version = "1.0",
        mixinStandardHelpOptions = true,
        subcommands = {
                HelpCommand.class,
                TodoStatus.class,
                TodoBackup.class,
                TodoRestore.class,
                TodoSort.class,
                TodoEdit.class,
                TodoRename.class,
                TodoClear.class,
                TodoList.class,
                TodoAdd.class,
                TodoDone.class,
                TodoRemove.class
        })
public class Todo implements Runnable {
    @Option(names = {"-g", "--global"}, description = "Use the global directory")
    private boolean global;

    @Option(names = {"-a", "--all"}, description = "Show completed items")
    private boolean showAll;

    @Option(names = {"-b", "--backup"}, description = "Backup before command is executed")
    private boolean backup;

    @Inject
    @Global
    private Path globalPath;

    @Inject
    @Local
    private Path localPath;

    public void run() {
        Path directory = getDirectory();

        if (Files.exists(directory)) {
            Console.info(String.format("Directory: %s", directory));
            CommandLine.usage(Todo.class, System.out);
        } else {
            System.out.println("No directory found (or any of the parent directories)");
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
        return getDirectory().resolve("todo.txt");
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
