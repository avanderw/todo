package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.file.TodoFile;
import net.avdw.todo.file.TodoFileFactory;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.template.TemplateExecutor;
import net.avdw.todo.template.TemplateViewModel;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.util.ArrayList;
import java.util.List;

@Command(name = "rm", description = "Remove a todo item")
public class TodoRemove implements Runnable {
    @ParentCommand
    private Todo todo;
    @Parameters(description = "Index to remove", arity = "1")
    private int idx;

    @Inject
    private TodoFileFactory todoFileFactory;
    @Inject
    private TodoFileWriter todoFileWriter;
    @Inject
    private TemplateExecutor templateExecutor;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        List<TodoItem> filteredList = new ArrayList<>();
        TodoFile fileBefore = todoFileFactory.create(todo.getTodoFile());

        List<TodoItem> allTodoItemList = new ArrayList<>(fileBefore.getTodoItemList().getAll());
        TodoItem removeItem = allTodoItemList.remove(idx - 1);
        filteredList.add(removeItem);
        TodoFile fileAfter = new TodoFile(fileBefore.getPath(), allTodoItemList);
        todoFileWriter.write(fileAfter);

        TemplateViewModel templateViewModel = new TemplateViewModel("remove", filteredList, fileBefore, fileAfter);
        System.out.println(templateExecutor.executor(templateViewModel));
    }

}
