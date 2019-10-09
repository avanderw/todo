package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.property.PropertyKey;
import net.avdw.todo.property.PropertyResolver;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.avdw.todo.render.ConsoleFormatting.h1;

@Command(name = "edit", description = "Open the configured editor for todo.txt")
public class TodoEdit implements Runnable {
    @ParentCommand
    private Todo todo;

    @Inject
    private PropertyResolver propertyResolver;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        h1("todo:edit");
        Path file = todo.getTodoFile();

        if (Files.exists(file)) {
            Logger.info(String.format("Opening configured editor for %s", file));
            ProcessBuilder pb = new ProcessBuilder(propertyResolver.resolve(PropertyKey.EDITOR_PATH), file.toString());
            try {
                pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
