package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Ansi;
import net.avdw.todo.Todo;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.TodoItemFactory;
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
import java.util.List;

import static net.avdw.todo.render.ConsoleFormatting.h1;

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
    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    private TodoFileWriter todoFileWriter;
    @Inject
    private TodoItemFactory todoItemFactory;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        h1("todo:add");
        if (date || Boolean.parseBoolean(propertyResolver.resolve(PropertyKey.TODO_ADD_AUTO_DATE))) {
            addition = String.format("%s %s", simpleDateFormat.format(new Date()), addition);
        }
        List<TodoItem> allTodoItems = todoFileReader.readAll(todo.getTodoFile());

        TodoItem additionalTodoItem = todoItemFactory.create(allTodoItems.size() + 1, addition);
        if (allTodoItems.stream().filter(TodoItem::isIncomplete).anyMatch(todoItem -> todoItem.rawValue().equals(addition))) {
            Logger.info(additionalTodoItem);
            Logger.warn("The todo item will not be added");
            Logger.info("Adding will create a duplicate");
        } else {
            allTodoItems.add(additionalTodoItem);
            todoFileWriter.write(allTodoItems, todo.getTodoFile());
            Logger.info(String.format("%sAdded%s: %s", Ansi.GREEN, Ansi.RESET, additionalTodoItem));
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
            Logger.error(String.format("Could not add `%s` to `%s`", rawValue, toFile));
            Logger.debug(e);
        }
    }
}
