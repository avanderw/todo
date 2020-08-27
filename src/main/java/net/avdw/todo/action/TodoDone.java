package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.MainCli;
import net.avdw.todo.file.TodoFile;
import net.avdw.todo.file.TodoFileFactory;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.TodoItemCompletor;
import net.avdw.todo.template.TemplateExecutor;
import net.avdw.todo.template.TemplateViewModel;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.util.ArrayList;
import java.util.List;

@Command(name = "do", description = "Complete a todo item")
public class TodoDone implements Runnable {
    @ParentCommand
    private MainCli mainCli;

    @Parameters(description = "Index to complete", arity = "1")
    private int idx;

    @Inject
    private TodoFileFactory todoFileFactory;
    @Inject
    private TodoFileWriter todoFileWriter;
    @Inject
    private TodoItemCompletor todoItemCompletor;
    @Inject
    private TemplateExecutor templateExecutor;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        List<TodoItem> filteredTodoItemList = new ArrayList<>();
        TodoFile fileBefore = todoFileFactory.create(mainCli.getTodoFile());
        TodoFile fileAfter;

        TodoItem todoItem = fileBefore.getTodoItemList().getAll().get(idx - 1);
        filteredTodoItemList.add(todoItem);
        if (todoItem.isComplete()) {
            Logger.warn("Item is already marked as done");
            fileAfter = fileBefore;
        } else {
            fileAfter = new TodoFile(fileBefore.getPath(), fileBefore.getTodoItemList().getAll());
            TodoItem completeItem = todoItemCompletor.complete(todoItem);
            filteredTodoItemList.add(completeItem);

            fileAfter.getTodoItemList().getAll().set(idx - 1, completeItem);
            todoFileWriter.write(fileAfter);
        }

        TemplateViewModel templateViewModel = new TemplateViewModel("done", filteredTodoItemList, fileBefore, fileAfter);
        System.out.println(templateExecutor.executor(templateViewModel));

    }
}
