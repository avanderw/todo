package net.avdw.todo.render;

import com.google.inject.Inject;
import net.avdw.todo.Ansi;
import net.avdw.todo.item.TodoItem;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static net.avdw.todo.render.ConsoleFormatting.hr;

public class TodoProjectTable {
    private static final double PERCENTAGE = 100.;
    private static final double UPPER_BOUND = 75.;
    private static final int NEW_LINE_COUNT_BREAK = 4;

    private TodoDoneStatusbar todoDoneStatusbar;

    @Inject
    TodoProjectTable(final TodoDoneStatusbar todoDoneStatusbar) {
        this.todoDoneStatusbar = todoDoneStatusbar;
    }

    /**
     * Print the project table for a todo list.
     *
     * @param todoItemList the todo list to print the table for
     */
    public void printProjectTable(final List<TodoItem> todoItemList) {
        Map<String, List<TodoItem>> projects = collectProjectTokenListMap(todoItemList);

        projects.forEach((key, value) -> {
            Collections.reverse(value);
            long done = value.stream().filter(TodoItem::isComplete).count();
            double percentage = done * PERCENTAGE / value.size();

            Logger.info(String.format("%s%16s%s ( %s%3.0f%%%s ): %s",
                    Ansi.PROJECT_COLOR, key, Ansi.RESET,
                    percentage > UPPER_BOUND ? Ansi.GREEN : "", percentage, Ansi.RESET,
                    todoDoneStatusbar.createBar(value)));
        });

        hr();
        long withProjects = todoItemList.stream().filter(TodoItem::hasProjects).count();
        Logger.info(String.format("%s projects, %s todo items", projects.size(), withProjects));
    }

    /**
     * Print a summary table of the project tags.
     *
     * @param todoItemList the list of todo items to collect project information from
     */
    public void printProjectSummaryTable(final List<TodoItem> todoItemList) {
        Map<String, List<TodoItem>> projects = collectProjectTokenListMap(todoItemList);
        StringBuilder stringBuilder = new StringBuilder("Projects: ");

        int newLineCount = 0;
        for (Map.Entry<String, List<TodoItem>> entry : projects.entrySet().stream()
                .sorted(Comparator.comparing(projectListEntry -> {
                    long completed = projectListEntry.getValue().stream().filter(TodoItem::isComplete).count();
                    return completed * PERCENTAGE / projectListEntry.getValue().size();
                }))
                .collect(Collectors.toList())) {
            if (newLineCount++ > NEW_LINE_COUNT_BREAK) {
                stringBuilder.append(String.format("%n%s", StringUtils.repeat(" ", "Projects: ".length())));
                newLineCount = 1;
            }

            long completed = entry.getValue().stream().filter(TodoItem::isComplete).count();
            double percentage = completed * PERCENTAGE / entry.getValue().size();
            String percent = String.format("%3.0f%%", percentage);
            stringBuilder.append(Ansi.PROJECT_COLOR);
            stringBuilder.append(String.format("%12s", entry.getKey()));
            stringBuilder.append(Ansi.RESET);
            stringBuilder.append(String.format("( %s%s%s )", Ansi.GREEN, percent, Ansi.RESET));
        }
        Logger.info(stringBuilder.toString());
    }

    private Map<String, List<TodoItem>> collectProjectTokenListMap(final List<TodoItem> todoItemList) {
        Map<String, List<TodoItem>> projects = new HashMap<>();
        todoItemList.forEach(todoItem
                -> todoItem.getProjects().forEach(project -> {
            projects.putIfAbsent(project, new ArrayList<>());
            projects.get(project).add(todoItem);
        }));
        return projects;
    }
}