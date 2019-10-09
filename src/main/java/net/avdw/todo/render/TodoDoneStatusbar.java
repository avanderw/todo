package net.avdw.todo.render;

import net.avdw.todo.Ansi;
import net.avdw.todo.item.TodoItem;

import java.util.Comparator;
import java.util.List;

public class TodoDoneStatusbar {
    private static final float PERCENTAGE = 100f;

    /**
     * Build a ANSI bar showing which items are complete vs incomplete.
     *
     * @param todoItemList the list of todo to create the bar from
     * @return the ANSI colour coded bar
     */
    public String createBar(final List<TodoItem> todoItemList) {
        StringBuilder stringBuilder = new StringBuilder();

        todoItemList.stream().sorted(Comparator.comparing(TodoItem::isComplete).reversed()).forEach(todoItem -> {
            stringBuilder.append(todoItem.isComplete() ? Ansi.GREEN : Ansi.WHITE);
            stringBuilder.append("#");
        });
        stringBuilder.append(Ansi.RESET);

        return stringBuilder.toString();
    }

    /**
     * Add percentage onto the completion bar.
     *
     * @param todoItemList the todo items from which to create the bar
     * @return the completion bar with percentage
     */
    public String createPercentageBar(final List<TodoItem> todoItemList) {
        String bar = createBar(todoItemList);
        long complete = todoItemList.stream().filter(TodoItem::isComplete).count();
        String percentage = String.format("%.0f", complete * PERCENTAGE / todoItemList.size());
        return String.format("%s%% [%s]", percentage, bar);
    }
}
