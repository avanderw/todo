package net.avdw.todo.admin;

import net.avdw.todo.Todo;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.avdw.todo.render.ConsoleFormatting.h1;

@Command(name = "explore", description = "Open the .todo directory")
public class TodoExplore implements Runnable {
    @ParentCommand
    private Todo todo;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        h1("todo:explore");
        Path directory = todo.findDirectory();

        if (Files.exists(directory)) {
            Logger.info(String.format("Exploring %s", directory));
            ProcessBuilder pb = new ProcessBuilder("explorer.exe", directory.toString());
            try {
                pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
