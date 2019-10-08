package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Ansi;
import net.avdw.todo.Console;
import net.avdw.todo.Todo;
import net.avdw.todo.TodoItemV1;
import net.avdw.todo.property.PropertyKey;
import net.avdw.todo.property.PropertyResolver;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

@Command(name = "add", description = "Add an item to todo.txt")
public class TodoAdd implements Runnable {
    @ParentCommand
    private Todo todo;

    @Parameters(description = "Text to add to the todo.txt file on its own line", arity = "1")
    private String addition;

    @Option(names = {"-d", "--date"}, description = "Prepend today's date to the line")
    private boolean date;

    @Inject
    private PropertyResolver propertyResolver;

    @Inject
    private SimpleDateFormat simpleDateFormat;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        if (date || Boolean.parseBoolean(propertyResolver.resolve(PropertyKey.TODO_ADD_AUTO_DATE))) {
            addition = String.format("%s %s", simpleDateFormat.format(new Date()), addition);
        }

        try (Scanner scanner = new Scanner(todo.getTodoFile())) {
            int lineNum = 0;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (!line.startsWith("x ")) {
                    lineNum++;
                }
            }
            lineNum++;

            add(todo.getTodoFile(), addition);
            Console.info(String.format("[%s%2s%s] %sAdded%s: %s", Ansi.BLUE, lineNum, Ansi.RESET, Ansi.GREEN, Ansi.RESET, new TodoItemV1(addition)));
        } catch (IOException e) {
            Console.error(String.format("Could not add `%s` to `%s`", todo.getTodoFile(), addition));
            Logger.error(e);
        }
    }

    /**
     * Append text to the end of a file.
     * The intention is to append a new todo in the file.
     *
     * @param toFile   the file to append to
     * @param rawValue the text value to append
     */
    public void add(final Path toFile, final String rawValue) {
        try (FileWriter fw = new FileWriter(toFile.toFile(), true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(rawValue);
        } catch (IOException e) {
            Console.error(String.format("Could not add `%s` to `%s`", rawValue, toFile));
            Logger.error(e);
        }
    }
}
