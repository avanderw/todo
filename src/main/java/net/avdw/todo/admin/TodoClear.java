package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.theme.ThemeApplicator;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@Command(name = "clear", description = "Clear the todo.txt file")
public class TodoClear implements Runnable {
    @ParentCommand
    private Todo todo;

    @Inject
    private ThemeApplicator themeApplicator;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        System.out.println(themeApplicator.header("todo:clear"));
        try {
            Files.write(todo.getTodoFile(), "".getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.info(String.format("Cleared file: %s", todo.getTodoFile()));
    }
}
