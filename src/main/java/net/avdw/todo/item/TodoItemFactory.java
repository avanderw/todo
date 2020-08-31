package net.avdw.todo.item;

@Deprecated
public interface TodoItemFactory {
    TodoItem create(int idx, String line);
}
