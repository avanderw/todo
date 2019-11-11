package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.theme.ThemeApplicator;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@Command(name = "replace", description = "Replace one string for another")
public class TodoReplace implements Runnable {
    @ParentCommand
    private Todo todo;

    @Parameters(description = "Regex against which to match",
            arity = "1", index = "0")
    private String from;

    @Parameters(description = "What to replace the matches with",
            arity = "1", index = "1")
    private String to;

    @Inject
    private ThemeApplicator themeApplicator;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        System.out.println(themeApplicator.header("todo:replace"));
        todo.backup();
        try {
            String contents = new String(Files.readAllBytes(todo.getTodoFile()));
            Files.write(todo.getTodoFile(), contents.replaceAll(from, to).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            Logger.info(String.format("Changed %s to %s", from, to));
        } catch (IOException e) {
            Logger.error(String.format("Error writing `%s`", to));
            Logger.debug(e);
        }
    }
}
