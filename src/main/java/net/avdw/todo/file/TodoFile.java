package net.avdw.todo.file;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import net.avdw.todo.item.TodoItem;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class TodoFile {
    private final Path path;
    private final List<TodoItem> allTodoItemList;
    private final List<TodoItem> completeTodoItemList;
    private final List<TodoItem> incompleteTodoItemList;

    @Inject
    TodoFile(@Assisted final Path path, final TodoFileReader todoFileReader) {
        this.path = path;
        this.allTodoItemList = todoFileReader.readAll(path);
        this.completeTodoItemList = allTodoItemList.stream().filter(TodoItem::isComplete).collect(Collectors.toList());
        this.incompleteTodoItemList = allTodoItemList.stream().filter(TodoItem::isIncomplete).collect(Collectors.toList());
    }

    public List<TodoItem> getAllTodoItemList() {
        return allTodoItemList;
    }

    public List<TodoItem> getCompleteTodoItemList() {
        return completeTodoItemList;
    }

    public List<TodoItem> getIncompleteTodoItemList() {
        return incompleteTodoItemList;
    }

    public Path getPath() {
        return path;
    }

    public Path getBackupPath() {
        return path.getParent().resolve(String.format("%s.bak", path.getFileName()));
    }
}
