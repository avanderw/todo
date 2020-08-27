package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.MainCli;
import net.avdw.todo.file.TodoFile;
import net.avdw.todo.file.TodoFileFactory;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.TodoItemModifier;
import net.avdw.todo.item.list.TodoItemListFilter;
import net.avdw.todo.template.TemplateExecutor;
import net.avdw.todo.template.TemplateViewModel;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Command(name = "repeat", description = "Do and add an entry to todo.txt")
public class TodoRepeat implements Runnable {
    @ParentCommand
    private MainCli mainCli;

    @Parameters(description = "Index of the entry to remove", arity = "1..*")
    private List<Integer> idxList;

    @Option(names = "--date", required = true, description = "Due date to add with the new entry")
    private Date dueDate;

    @Inject
    private TodoFileFactory todoFileFactory;
    @Inject
    private TodoItemListFilter todoItemListFilter;
    @Inject
    private TodoFileWriter todoFileWriter;
    @Inject
    private TodoItemModifier todoItemModifier;
    @Inject
    private TemplateExecutor templateExecutor;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        TodoFile fileBefore = todoFileFactory.create(mainCli.getTodoFile());
        List<TodoItem> filteredTodoItemList = todoItemListFilter.filterByIdx(fileBefore, idxList);

        List<TodoItem> completedTodoItemList = filteredTodoItemList.stream()
                .map(todoItem -> todoItemModifier.complete(todoItem)).collect(Collectors.toList());

        AtomicInteger total = new AtomicInteger(fileBefore.getTodoItemList().getAll().size() + 1);
        List<TodoItem> additionalTodoItemList = filteredTodoItemList.stream().map(todoItem -> {
            TodoItem modifiedTodoItem = todoItemModifier.stripCompletionDate(todoItem);
            modifiedTodoItem = todoItemModifier.stripPriority(modifiedTodoItem);
            modifiedTodoItem = todoItemModifier.stripStartDate(modifiedTodoItem);
            modifiedTodoItem = todoItemModifier.changeDueDate(modifiedTodoItem, dueDate);
            modifiedTodoItem = todoItemModifier.addStartDate(modifiedTodoItem);
            modifiedTodoItem = todoItemModifier.changeIdx(modifiedTodoItem, total.getAndIncrement());
            return modifiedTodoItem;
        }).collect(Collectors.toList());

        List<TodoItem> modifiedTodoItemList = new ArrayList<>(fileBefore.getTodoItemList().getAll());
        completedTodoItemList.forEach(todoItem -> {
            modifiedTodoItemList.set(todoItem.getIdx() - 1, todoItem);
        });
        modifiedTodoItemList.addAll(additionalTodoItemList);

        TodoFile fileAfter = new TodoFile(fileBefore.getPath(), modifiedTodoItemList);
        todoFileWriter.write(fileAfter);

        TemplateViewModel templateViewModel = new TemplateViewModel("repeat", modifiedTodoItemList, fileBefore, fileAfter);
        System.out.println(templateExecutor.executor(templateViewModel));
    }

}
