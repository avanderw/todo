package net.avdw.todo.theme;

import net.avdw.todo.item.TodoItem;

import java.util.List;

public interface Theme {
    void printHeader(String text);

    void printDuration();

    void printTodoItemList(List<TodoItem> todoItemList);
}
