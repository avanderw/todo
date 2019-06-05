package net.avdw.todo.list.rewriting;

public class ReplaceEvent {
    final Integer idx;
    final String todoItem;

    public ReplaceEvent(Integer idx, String todoItem) {
        this.idx = idx;
        this.todoItem = todoItem;
    }
}
