package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.Global;
import net.avdw.todo.LocalTodo;
import net.avdw.todo.MainCli;
import net.avdw.todo.file.TodoFile;
import net.avdw.todo.file.TodoFileFactory;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.template.TemplateExecutor;
import net.avdw.todo.template.TemplateViewModel;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Deprecated
@Command(name = "migrate", description = "Move todo between local and global")
public class TodoMigrate implements Runnable {

    @ParentCommand
    private MainCli mainCli;

    @Parameters(description = "Index to migrate", arity = "1")
    private int idx;

    @Inject
    private TodoFileFactory todoFileFactory;

    @Inject
    @Global
    private Path globalPath;

    @Inject
    @LocalTodo
    private Path localPath;
    @Inject
    private TemplateExecutor templateExecutor;
    @Inject
    private TodoFileWriter todoFileWriter;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        Path fromDirectory = mainCli.isGlobal() ? globalPath : localPath;
        Path toDirectory = mainCli.isGlobal() ? localPath : globalPath;
        TodoFile fromFile = todoFileFactory.create(fromDirectory.resolve("todo.txt"));
        TodoFile toFile = todoFileFactory.create(toDirectory.resolve("todo.txt"));

        List<TodoItem> viewableTodoItemList = new ArrayList<>();
        TodoItem migrateItem = fromFile.getTodoItemList().getAll().get(idx - 1);
        viewableTodoItemList.add(migrateItem);

        List<TodoItem> allFromTodoItemList = new ArrayList<>(fromFile.getTodoItemList().getAll());
        allFromTodoItemList.remove(migrateItem);
        List<TodoItem> allToTodoItemList = new ArrayList<>(toFile.getTodoItemList().getAll());
        allToTodoItemList.add(migrateItem);

        todoFileWriter.write(new TodoFile(fromFile.getPath(), allFromTodoItemList));
        todoFileWriter.write(new TodoFile(toFile.getPath(), allToTodoItemList));

        TemplateViewModel templateViewModel = new TemplateViewModel("admin/migrate", viewableTodoItemList, fromFile, toFile);
        System.out.println(templateExecutor.executor(templateViewModel));
    }
}
