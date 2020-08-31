package net.avdw.todo.render;

import net.avdw.todo.item.TodoItem;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class TodoFileStatusModel {
    private String path = "some path";
    private List<TodoItem> pathItems = new ArrayList<>();

    public String getPath() {
        return path;
    }

    public List<TodoItem> getPathItems() {
        return pathItems;
    }
}
