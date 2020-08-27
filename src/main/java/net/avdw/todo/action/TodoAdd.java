package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.MainCli;
import net.avdw.todo.file.TodoFile;
import net.avdw.todo.file.TodoFileFactory;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.TodoItemFactory;
import net.avdw.todo.property.PropertyKey;
import net.avdw.todo.property.PropertyResolver;
import net.avdw.todo.template.TemplateExecutor;
import net.avdw.todo.template.TemplateViewModel;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @see net.avdw.todo.AddCli
 */
@Deprecated
@Command(name = "add", description = "Add an item to todo.txt")
public class TodoAdd implements Runnable {
    @ParentCommand
    private MainCli mainCli;

    @Parameters(description = "Text to add to the todo.txt file on its own line", arity = "1")
    private String addition;

    @Option(names = {"-d", "--date"}, description = "Prepend today's date to the line")
    private boolean date;

    @Inject
    private PropertyResolver propertyResolver;
    @Inject
    private SimpleDateFormat simpleDateFormat;
    @Inject
    private TodoFileWriter todoFileWriter;
    @Inject
    private TodoItemFactory todoItemFactory;
    @Inject
    private TemplateExecutor templateExecutor;
    @Inject
    private TodoFileFactory todoFileFactory;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        if (date || Boolean.parseBoolean(propertyResolver.resolve(PropertyKey.TODO_ADD_AUTO_DATE))) {
            addition = String.format("%s %s", simpleDateFormat.format(new Date()), addition);
        }

        List<TodoItem> filteredList = new ArrayList<>();
        TodoFile fileBefore = todoFileFactory.create(mainCli.getTodoFile());
        TodoFile fileAfter = fileBefore;
        TodoItem addTodoItem = todoItemFactory.create(fileBefore.getTodoItemList().getAll().size() + 1, addition);
        filteredList.add(addTodoItem);
        if (fileBefore.getTodoItemList().getAll().stream().anyMatch(todoItem -> todoItem.getRawValue().equals(addition))) {
            Logger.warn("The todo item will not be added as it will create a duplicate");
        } else {
            List<TodoItem> allItems = new ArrayList<>(fileBefore.getTodoItemList().getAll());
            allItems.add(addTodoItem);
            fileAfter = new TodoFile(fileBefore.getPath(), allItems);
            todoFileWriter.write(fileAfter);
        }

        TemplateViewModel templateViewModel = new TemplateViewModel("add", filteredList, fileBefore, fileAfter);
        System.out.println(templateExecutor.executor(templateViewModel));
    }

}
