package net.avdw.todo.render;

import com.google.inject.Inject;
import net.avdw.todo.AnsiColor;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.theme.ThemeApplicator;

import java.util.Comparator;
import java.util.List;

public class TodoDoneStatusbar {
    private final PercentageRenderer percentageRenderer;
    private final ThemeApplicator themeApplicator;

    @Inject
    TodoDoneStatusbar(final PercentageRenderer percentageRenderer, final ThemeApplicator themeApplicator) {
        this.percentageRenderer = percentageRenderer;
        this.themeApplicator = themeApplicator;
    }

    /**
     * Build a ANSI bar showing which items are complete vs incomplete.
     *
     * @param todoItemList the list of todo to create the bar from
     * @return the ANSI colour coded bar
     */
    public String createBar(final List<TodoItem> todoItemList) {
        StringBuilder stringBuilder = new StringBuilder();

        todoItemList.stream().sorted(Comparator.comparing(TodoItem::isComplete).reversed()).forEach(todoItem -> {
            stringBuilder.append(todoItem.isComplete() ? themeApplicator.blockComplete() : themeApplicator.blockIncomplete());
        });
        stringBuilder.append(AnsiColor.RESET);

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
        double complete = todoItemList.stream().filter(TodoItem::isComplete).count() * 1.;
        return String.format("%s %s", percentageRenderer.renderText(complete / todoItemList.size()), bar);
    }
}
