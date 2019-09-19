package net.avdw.todo.admin;

import net.avdw.todo.Console;
import net.avdw.todo.Todo;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Command(name = "explore", description = "Open the .todo directory")
public class TodoExplore implements Runnable {
    @ParentCommand
    private Todo todo;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        Path directory = todo.findDirectory();

        if (Files.exists(directory)) {
            Console.info(String.format("Exploring %s", directory));
            ProcessBuilder pb = new ProcessBuilder("explorer.exe", directory.toString());
            try {
                pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
