package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Removed;
import net.avdw.todo.Todo;
import net.avdw.todo.Working;
import net.avdw.todo.file.TodoFile;
import net.avdw.todo.file.TodoFileFactory;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.template.TemplateExecutor;
import net.avdw.todo.template.TemplateViewModel;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Command(name = "rm", description = "Remove a todo item")
public class TodoRemove implements Runnable {
    @ParentCommand
    private Todo todo;
    @Parameters(description = "Indexes to remove", arity = "1..*")
    private List<Integer> idxList;

    @Inject
    private TodoFileFactory todoFileFactory;
    @Inject
    private TodoFileWriter todoFileWriter;
    @Inject
    private TemplateExecutor templateExecutor;
    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    @Working
    private Path todoFilePath;
    @Inject
    @Removed
    private Path removedFilePath;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        List<TodoItem> changedTodoItemList = new ArrayList<>();
        List<TodoItem> todoItemList = todoFileReader.readAll(todoFilePath);

        idxList.stream().sorted(Comparator.reverseOrder())
                .forEachOrdered(idx -> {
                    TodoItem removeItem = todoItemList.remove(idx - 1);
                    changedTodoItemList.add(removeItem);
                });
        todoFileWriter.write(todoFilePath, todoItemList);

        List<TodoItem> removedTodoItemList;
        if (Files.exists(removedFilePath)) {
            removedTodoItemList = todoFileReader.readAll(removedFilePath);
        } else {
            removedTodoItemList = new ArrayList<>();
        }
        removedTodoItemList.addAll(changedTodoItemList);
        todoFileWriter.write(removedFilePath, removedTodoItemList);

        TodoFile fileBefore = todoFileFactory.create(todo.getTodoFile());
        TemplateViewModel templateViewModel = new TemplateViewModel("remove", changedTodoItemList, fileBefore, fileBefore);
        System.out.println(templateExecutor.executor(templateViewModel));
    }
}
