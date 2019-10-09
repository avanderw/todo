package net.avdw.todo.item;

public interface TodoItemFactory {
    TodoItem create(int idx, String line);
}
