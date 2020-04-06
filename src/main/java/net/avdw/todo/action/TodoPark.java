package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Parked;
import net.avdw.todo.Working;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.theme.Theme;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Command(name = "park", description = "Park a todo item for later review")
public class TodoPark implements Runnable {
    @Parameters(description = "Indexes to park", arity = "1..*")
    private List<Integer> idxList;

    @Inject
    private TodoFileWriter todoFileWriter;
    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    @Working
    private Path todoFilePath;
    @Inject
    @Parked
    private Path parkedFilePath;
    @Inject
    private Theme theme;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        List<TodoItem> changedTodoItemList = new ArrayList<>();
        List<TodoItem> todoItemList = todoFileReader.readAll(todoFilePath);

        idxList.stream().sorted(Comparator.reverseOrder())
                .forEachOrdered(idx -> {
                    TodoItem parkedItem = todoItemList.remove(idx - 1);
                    changedTodoItemList.add(parkedItem);
                });
        todoFileWriter.write(todoFilePath, todoItemList);

        List<TodoItem> parkedTodoItemList;
        if (Files.exists(parkedFilePath)) {
            parkedTodoItemList = todoFileReader.readAll(parkedFilePath);
        } else {
            parkedTodoItemList = new ArrayList<>();
        }
        parkedTodoItemList.addAll(changedTodoItemList);
        todoFileWriter.write(parkedFilePath, parkedTodoItemList);

        theme.printHeader("park");
        changedTodoItemList.forEach(theme::printFullTodoItemWithIdx);
        theme.printDuration();
    }
}
