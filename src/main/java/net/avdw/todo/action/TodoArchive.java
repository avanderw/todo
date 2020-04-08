package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Done;
import net.avdw.todo.Working;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.theme.Theme;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CommandLine.Command(name = "archive", description = "Move done items to done.txt")
public class TodoArchive implements Runnable {
    @Inject
    private TodoFileWriter todoFileWriter;
    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    @Working
    private Path todoFilePath;
    @Inject
    @Done
    private Path doneFilePath;
    @Inject
    private Theme theme;

    @Override
    public void run() {
        List<TodoItem> todoItemList = todoFileReader.readAll(todoFilePath);
        List<TodoItem> doneItemList = todoItemList.stream().filter(TodoItem::isComplete).collect(Collectors.toList());
        todoItemList.removeAll(doneItemList);
        todoFileWriter.write(todoFilePath, todoItemList);

        List<TodoItem> removedTodoItemList;
        if (Files.exists(doneFilePath)) {
            removedTodoItemList = todoFileReader.readAll(doneFilePath);
        } else {
            removedTodoItemList = new ArrayList<>();
        }
        removedTodoItemList.addAll(doneItemList);
        todoFileWriter.write(doneFilePath, removedTodoItemList);

        theme.printHeader("archive");
        doneItemList.forEach(theme::printFullTodoItemWithIdx);
        theme.printDuration();
    }
}
