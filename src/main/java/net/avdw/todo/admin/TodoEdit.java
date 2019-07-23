package net.avdw.todo.admin;

import net.avdw.todo.Console;
import net.avdw.todo.Todo;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Command(name = "edit", description = "Open the configured editor for todo.txt")
public class TodoEdit implements Runnable {
    @ParentCommand
    private Todo todo;

    @Override
    public void run() {
        Path file = todo.getTodoFile();

        if (Files.exists(file)) {
            Console.info(String.format("Edit %s", file));
            ProcessBuilder pb = new ProcessBuilder("notepad.exe", file.toString());
            try {
                pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
