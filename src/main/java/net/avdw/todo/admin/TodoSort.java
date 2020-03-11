package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.file.TodoFile;
import net.avdw.todo.file.TodoFileFactory;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.TodoItemFactory;
import net.avdw.todo.template.TemplateExecutor;
import net.avdw.todo.template.TemplateViewModel;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

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

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        TodoFile fileBefore = todoFileFactory.create(todo.getTodoFile());
        TodoFile fileAfter = new TodoFile(fileBefore.getPath(), fileBefore.getTodoItemList().getAll().stream().sorted(Comparator.comparing(TodoItem::getRawValue)).collect(Collectors.toList()));
        todoFileWriter.write(fileAfter);

        List<TodoItem> sortedList = fileAfter.getTodoItemList().getIncomplete();
        for (int i = 1; i <= sortedList.size(); i++) {
            sortedList.set(i - 1, todoItemFactory.create(i, sortedList.get(i - 1).getRawValue()));
        }

        TemplateViewModel templateViewModel = new TemplateViewModel("sort", sortedList, fileBefore, fileAfter);
        System.out.println(templateExecutor.executor(templateViewModel));
    }
}
