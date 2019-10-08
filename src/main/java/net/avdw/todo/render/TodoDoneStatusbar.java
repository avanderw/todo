package net.avdw.todo.render;

import net.avdw.todo.Ansi;
import net.avdw.todo.TodoItem;

import java.util.List;

public class TodoDoneStatusbar {
    /**
     * Build a ANSI bar showing which items are complete vs incomplete.
     * @param todoItemList the list of todo to create the bar from
     * @return the ANSI colour coded bar
     */
    public String createBar(final List<TodoItem> todoItemList) {
        StringBuilder stringBuilder = new StringBuilder();

        todoItemList.forEach(todoItem -> {
            stringBuilder.append(todoItem.isDone() ? Ansi.GREEN : Ansi.WHITE);
            stringBuilder.append("#");
        });
        stringBuilder.append(Ansi.RESET);

        return stringBuilder.toString();
    }
}
