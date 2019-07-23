package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.*;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.nio.file.Files;
import java.nio.file.Path;

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

    @Override
    public void run() {
        Console.h1("Todo Status");
        Console.info(String.format("Local  : %s", Files.exists(localPath) ? localPath : "todo init"));
        Console.info(String.format("Global : %s", Files.exists(globalPath) ? globalPath : "todo init --global"));
        Console.info(String.format("Default: %s", todo.getDirectory()));

        if (!Files.exists(localPath) || !Files.exists(globalPath)) {
            CommandLine.usage(TodoInit.class, System.out);
        }
    }
}
