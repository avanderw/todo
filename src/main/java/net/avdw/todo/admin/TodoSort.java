package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.Working;
import net.avdw.todo.file.TodoFile;
import net.avdw.todo.file.TodoFileFactory;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.TodoItemFactory;
import net.avdw.todo.template.TemplateExecutor;
import net.avdw.todo.template.TemplateViewModel;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Command(name = "sort", description = "Sort todo.txt")
public class TodoSort implements Runnable {
    @ParentCommand
    private Todo todo;

    @Inject
    private TodoFileWriter todoFileWriter;
    @Inject
    private TodoFileFactory todoFileFactory;
    @Inject
    private TodoItemFactory todoItemFactory;
    @Inject
    private TemplateExecutor templateExecutor;
    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    @Working
    private Path todoPath;

    @CommandLine.Parameters(description = "Add these keys together to sort by")
    private List<String> sortKeys = new ArrayList<>();

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        List<TodoItem> todoItemList = todoFileReader.readAll(todoPath);
        List<TodoItem> incompleteItemList = todoItemList.stream().filter(TodoItem::isIncomplete).collect(Collectors.toList());
        List<TodoItem> completeItemList = todoItemList.stream().filter(TodoItem::isComplete).collect(Collectors.toList());

        List<TodoItem> sortedItemList;
        if (sortKeys.isEmpty()) {
            todoItemList.sort(Comparator.comparing(TodoItem::getRawValue));
            sortedItemList = todoItemList;
        } else {
            incompleteItemList.sort(Comparator.comparingInt(item -> sortKeys.stream().mapToInt(key -> {
                try {
                    return -Integer.parseInt(item.getMetaValueFor(key)); // refactor to use reversed
                } catch (RuntimeException e) {
                    return 0;
                }
            }).sum()));
            completeItemList.sort(Comparator.comparing(TodoItem::getRawValue));
            sortedItemList = incompleteItemList;
            sortedItemList.addAll(completeItemList);
        }

        TodoFile fileBefore = todoFileFactory.create(todo.getTodoFile());
        TodoFile fileAfter = new TodoFile(fileBefore.getPath(), sortedItemList);
        todoFileWriter.write(fileAfter);

        List<TodoItem> sortedList = fileAfter.getTodoItemList().getIncomplete();
        for (int i = 1; i <= sortedList.size(); i++) {
            sortedList.set(i - 1, todoItemFactory.create(i, sortedList.get(i - 1).getRawValue()));
        }

        TemplateViewModel templateViewModel = new TemplateViewModel("sort", sortedList, fileBefore, fileAfter);
        System.out.println(templateExecutor.executor(templateViewModel));
    }
}
