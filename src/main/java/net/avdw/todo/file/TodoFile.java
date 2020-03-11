package net.avdw.todo.file;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.list.TodoItemList;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TodoFile {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final Path path;
    private final TodoItemList todoItemList;

    @Inject
    public TodoFile(@Assisted final Path path, final TodoFileReader todoFileReader) {
        this.path = path;
        todoItemList = new TodoItemList(todoFileReader.readAll(path));
    }

    public TodoFile(final Path path, final List<TodoItem> todoItemList) {
        this.path = path;
        this.todoItemList = new TodoItemList(todoItemList);
    }

    public Path getPath() {
        return path;
    }

    public TodoItemList getTodoItemList() {
        return todoItemList;
    }


    public String getPathLastModified() {
        return SIMPLE_DATE_FORMAT.format(new Date(getPath().toFile().lastModified()));
    }

    public String getCompletePercent() {
        return "" + (todoItemList.getComplete().size() * 100 / todoItemList.getAll().size());
    }

    public Path getBackupPath() {
        return path.getParent().resolve(String.format("%s.bak", path.getFileName()));
    }

    public String getBackupPathLastModified() {
        return SIMPLE_DATE_FORMAT.format(new Date(getBackupPath().toFile().lastModified()));
    }
}
