package net.avdw.todo;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Command(name = "edit")
public class TodoEdit implements Runnable {
    @ParentCommand
    private Todo todo;

    @Override
    public void run() {
        Path path = todo.getDirectory();
        Path file = path.resolve("todo.txt");

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
