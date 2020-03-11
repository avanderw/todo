package net.avdw.todo.item.list;

import net.avdw.todo.item.TodoItem;

import java.util.List;
import java.util.stream.Collectors;

public class TodoItemList {
    private List<TodoItem> all;
    private List<TodoItem> complete;
    private List<TodoItem> incomplete;
    private List<TodoItem> priority;

    public TodoItemList(final List<TodoItem> all) {
        this.all = all;
    }

    public List<TodoItem> getAll() {
        return all;
    }

    public List<TodoItem> getComplete() {
        if (complete == null) {
            complete = all.stream().filter(TodoItem::isComplete).collect(Collectors.toList());
        }
        return complete;
    }

    public List<TodoItem> getIncomplete() {
        if (incomplete == null) {
            incomplete = all.stream().filter(TodoItem::isIncomplete).collect(Collectors.toList());
        }
        return incomplete;
    }

    public List<TodoItem> getPriority() {
        if (priority == null) {
            priority = all.stream().filter(TodoItem::hasPriority).collect(Collectors.toList());
        }

        return priority;
    }
}
