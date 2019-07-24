package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.action.TodoAdd;
import net.avdw.todo.action.TodoList;
import net.avdw.todo.admin.TodoBackup;
import net.avdw.todo.admin.TodoEdit;
import net.avdw.todo.admin.TodoInit;
import net.avdw.todo.admin.TodoStatus;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;

import java.nio.file.Files;
import java.nio.file.Path;

@Command(name = "todo",
        description = "The procrastination tool",
        subcommands = {
                HelpCommand.class,
                TodoStatus.class,
                TodoBackup.class,
                TodoEdit.class,
                TodoList.class,
                TodoAdd.class
        })
public class Todo implements Runnable {
    @Option(names = {"-g", "--global"}, description = "Target the global directory")
    private boolean global;

    @Inject
    @Global
    private Path globalPath;

    @Inject
    @Local
    private Path localPath;

    public void run() {
        Path directory = getDirectory();

        if (Files.exists(directory)) {
            Console.info(String.format("Repository: %s", directory));
            CommandLine.usage(Todo.class, System.out);
        } else {
            System.out.println("No repository found (or any of the parent directories)");
            CommandLine.usage(TodoInit.class, System.out);
        }
    }

    public Path getDirectory() {
        return global ? globalPath : localPath;
    }
    public Path getTodoFile() {
        return getDirectory().resolve("todo.txt");
    }

    public Path getBackupFile() {
        return getDirectory().resolve("todo.txt.bak");
    }
}
