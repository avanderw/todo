package net.avdw.todo.admin;

import net.avdw.todo.Console;
import net.avdw.todo.Todo;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

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

        }
    }
}
