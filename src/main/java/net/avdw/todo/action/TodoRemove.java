package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Removed;
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

/**
 * @see net.avdw.todo.RemoveCli
 */
@Deprecated
@Command(name = "rm", description = "Remove a todo item")
public class TodoRemove implements Runnable {
    @Parameters(description = "Indexes to remove", arity = "0..*")
    private List<Integer> idxList;

    @Inject
    private TodoFileWriter todoFileWriter;
    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    @Working
    private Path todoFilePath;
    @Inject
    @Removed
    private Path removedFilePath;
    @Inject
    private Theme theme;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        List<TodoItem> viewableTodoItemList = new ArrayList<>();
        if (idxList == null || idxList.isEmpty()) {
            viewableTodoItemList.addAll(todoFileReader.readAll(removedFilePath));
        } else {
            List<TodoItem> todoItemList = todoFileReader.readAll(todoFilePath);

            idxList.stream().sorted(Comparator.reverseOrder())
                    .forEachOrdered(idx -> {
                        TodoItem removeItem = todoItemList.remove(idx - 1);
                        viewableTodoItemList.add(removeItem);
                    });
            todoFileWriter.write(todoFilePath, todoItemList);

            List<TodoItem> removedTodoItemList;
            if (Files.exists(removedFilePath)) {
                removedTodoItemList = todoFileReader.readAll(removedFilePath);
            } else {
                removedTodoItemList = new ArrayList<>();
            }
            removedTodoItemList.addAll(viewableTodoItemList);
            todoFileWriter.write(removedFilePath, removedTodoItemList);
        }

        theme.printHeader("remove");
        viewableTodoItemList.forEach(theme::printFullTodoItemWithIdx);
        theme.printDuration();
    }
}
