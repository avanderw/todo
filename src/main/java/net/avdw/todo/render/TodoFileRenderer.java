package net.avdw.todo.render;

import net.avdw.todo.item.TodoItem;

import java.util.List;

public class TodoFileRenderer {
    /**
     * Render a one line summary for the todo list.
     *
     * @param todoItemList the todo list to render the summary for
     * @return a string representation of the todo list
     */
    public String renderOneLineSummary(final List<TodoItem> todoItemList) {
        long complete = todoItemList.stream().filter(TodoItem::isComplete).count();
        return String.format("[%s/%s]", complete, todoItemList.size());
    }
}
