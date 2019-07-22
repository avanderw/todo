package net.avdw.todo;

import com.google.inject.Inject;
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
                TodoEdit.class
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

    Path getDirectory() {
        return global ? globalPath : localPath;
    }
}
