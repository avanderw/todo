package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Working;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.TodoItemFactory;
import net.avdw.todo.theme.Theme;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@CommandLine.Command(name = "edit", description = "Edit an item at an index")
public class TodoEdit implements Runnable {
    @CommandLine.Parameters(description = "The index list to edit", arity = "1..*")
    private List<Integer> idxList;
    @CommandLine.Option(names = "--add", description = "Add the Strings to the todo")
    private List<String> addStringList = new ArrayList<>();
    @CommandLine.Option(names = "--remove", description = "Remove the Strings from the todo")
    private List<String> removeStringList = new ArrayList<>();

    @Inject
    private TodoItemFactory todoItemFactory;
    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    private TodoFileWriter todoFileWriter;
    @Inject
    private Theme theme;
    @Inject
    @Working
    private Path todoPath;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        List<TodoItem> changedTodoItemList = new ArrayList<>();
        List<TodoItem> todoItemList = todoFileReader.readAll(todoPath);
        idxList.forEach(idx -> {
            TodoItem todoItem = todoItemList.get(idx - 1);
            changedTodoItemList.add(todoItem);

            String rawValue = todoItem.getRawValue();
            for (String string : removeStringList) {
                rawValue = rawValue.replace(string, "");
            }

            for (String string : addStringList) {
                rawValue = String.format("%s %s", rawValue, string);
            }

            rawValue = rawValue.replaceAll("\\s+", " ");
            todoItemList.set(idx - 1, todoItemFactory.create(todoItem.getIdx(), rawValue));
            changedTodoItemList.add(todoItemList.get(idx - 1));
        });
        todoFileWriter.write(todoPath, todoItemList);

        theme.printHeader("edit");
        theme.printTodoItemList(changedTodoItemList);
        theme.printDuration();
    }
}
