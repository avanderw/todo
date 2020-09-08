package net.avdw.todo.theme;

import net.avdw.todo.item.TodoItem;

@Deprecated
public interface Theme {
    void printHeader(String text);

    void printDuration();

    void printCleanTodoItemWithoutIdx(TodoItem todoItem);

    void printFullTodoItemWithIdx(TodoItem todoItem);

    void printDisplaySummary(int showingSize, int totalSize);
}
