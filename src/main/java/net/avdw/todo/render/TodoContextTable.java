package net.avdw.todo.render;

import com.google.inject.Inject;
import net.avdw.todo.AnsiColor;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.theme.ThemeApplicator;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class TodoContextTable {
    private static final double PERCENTAGE = 100.;
    private static final double UPPER_BOUND = 75.;
    private static final int NEW_LINE_COUNT_BREAK = 4;

    private final TodoDoneStatusbar todoDoneStatusbar;
    private final ThemeApplicator themeApplicator;

    @Inject
    TodoContextTable(final TodoDoneStatusbar todoDoneStatusbar, final ThemeApplicator themeApplicator) {
        this.todoDoneStatusbar = todoDoneStatusbar;
        this.themeApplicator = themeApplicator;
    }

    /**
     * Print the context's for the todo list.
     *
     * @param todoItemList the todo list to collect the contexts from
     */
    public void printContextTable(final List<TodoItem> todoItemList) {
        Map<String, List<TodoItem>> contexts = collectContextListMap(todoItemList);

        contexts.forEach((key, value) -> {
            Collections.reverse(value);
            long done = value.stream().filter(TodoItem::isComplete).count();
            double percentage = done * PERCENTAGE / value.size();

            Logger.info(String.format("%s%16s%s ( %s%3.0f%%%s ): %s",
                    AnsiColor.CONTEXT_COLOR, key, AnsiColor.RESET,
                    percentage > UPPER_BOUND ? AnsiColor.GREEN : "", percentage, AnsiColor.RESET,
                    todoDoneStatusbar.createBar(value)));
        });
        System.out.println(themeApplicator.hr());
        long withContext = todoItemList.stream().filter(TodoItem::hasContext).count();
        Logger.info(String.format("%s contexts, %s todo items", contexts.size(), withContext));
    }

    /**
     * Print a summary table of the context tags.
     *
     * @param todoItemList the list of todo items to collect context information from
     */
    public void printContextSummaryTable(final List<TodoItem> todoItemList) {
        Map<String, List<TodoItem>> contexts = collectContextListMap(todoItemList);
        StringBuilder stringBuilder = new StringBuilder("Contexts: ");

        int newLineCount = 0;
        for (Map.Entry<String, List<TodoItem>> entry : contexts.entrySet().stream()
                .sorted(Comparator.comparing(projectListEntry -> {
                    long completed = projectListEntry.getValue().stream().filter(TodoItem::isComplete).count();
                    return completed * PERCENTAGE / projectListEntry.getValue().size();
                }))
                .collect(Collectors.toList())) {
            if (newLineCount++ > NEW_LINE_COUNT_BREAK) {
                stringBuilder.append(String.format("%n%s", StringUtils.repeat(" ", "Contexts: ".length())));
                newLineCount = 1;
            }

            long completed = entry.getValue().stream().filter(TodoItem::isComplete).count();
            double percentage = completed * PERCENTAGE / entry.getValue().size();
            String percent = String.format("%3.0f%%", percentage);
            stringBuilder.append(AnsiColor.CONTEXT_COLOR);
            stringBuilder.append(String.format("%12s", entry.getKey()));
            stringBuilder.append(AnsiColor.RESET);
            stringBuilder.append(String.format("( %s%s%s )", AnsiColor.GREEN, percent, AnsiColor.RESET));
        }
        Logger.info(stringBuilder.toString());
    }

    private Map<String, List<TodoItem>> collectContextListMap(final List<TodoItem> todoItemList) {
        Map<String, List<TodoItem>> contexts = new HashMap<>();
        todoItemList.forEach(todoItem
                -> todoItem.getContexts().forEach(context -> {
            contexts.putIfAbsent(context, new ArrayList<>());
            contexts.get(context).add(todoItem);
        }));
        return contexts;
    }

}
