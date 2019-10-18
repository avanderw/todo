package net.avdw.todo.render;

import com.google.inject.Inject;
import net.avdw.todo.AnsiColor;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.theme.ThemeApplicator;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class TodoProjectRenderer {
    private static final double PERCENTAGE = 100.;
    private static final double UPPER_BOUND = 75.;
    private static final int NEW_LINE_COUNT_BREAK = 4;

    private final TodoDoneStatusbar todoDoneStatusbar;
    private final ThemeApplicator themeApplicator;

    @Inject
    TodoProjectRenderer(final TodoDoneStatusbar todoDoneStatusbar, final ThemeApplicator themeApplicator) {
        this.todoDoneStatusbar = todoDoneStatusbar;
        this.themeApplicator = themeApplicator;
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
                    AnsiColor.PROJECT_COLOR, key, AnsiColor.RESET,
                    percentage > UPPER_BOUND ? AnsiColor.GREEN : "", percentage, AnsiColor.RESET,
                    todoDoneStatusbar.createBar(value)));
        });

        System.out.println(themeApplicator.hr());
        long withProjects = todoItemList.stream().filter(TodoItem::hasProjects).count();
        Logger.info(String.format("%s projects, %s todo items", projects.size(), withProjects));
    }

    /**
     * Print a summary table of the project tags.
     *
     * @param todoItemList the list of todo items to collect project information from
     */
    public void renderSummaryTable(final List<TodoItem> todoItemList) {
        Map<String, List<TodoItem>> projects = collectProjectTokenListMap(todoItemList);
        StringBuilder stringBuilder = new StringBuilder("Projects: ");

        int newLineCount = 0;
        for (Map.Entry<String, List<TodoItem>> entry : projects.entrySet().stream()
                .sorted(Comparator.comparing(projectListEntry -> {
                    double completed = projectListEntry.getValue().stream().filter(TodoItem::isComplete).count();
                    return completed / projectListEntry.getValue().size();
                }))
                .collect(Collectors.toList())) {
            if (newLineCount++ > NEW_LINE_COUNT_BREAK) {
                stringBuilder.append(String.format("%n%s", StringUtils.repeat(" ", "Projects: ".length())));
                newLineCount = 1;
            }

            double completed = entry.getValue().stream().filter(TodoItem::isComplete).count();
            double progress = completed / entry.getValue().size();
            stringBuilder.append(themeApplicator.project(String.format("%12s", entry.getKey())));
            String percentage = String.format("%3.0f%%", progress * PERCENTAGE);
            stringBuilder.append(String.format("( %s )", themeApplicator.progress(percentage, progress)));
        }
        System.out.println(stringBuilder.toString());
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

    /**
     * Provide a one-line summary of the projects.
     *
     * @param todoItemList the todo list to summarise
     * @return a string render of the summary
     */
    public String renderOneLineSummary(final List<TodoItem> todoItemList) {
        Map<String, List<TodoItem>> projectListMap = collectProjectTokenListMap(todoItemList);
        return String.format("%s projects", projectListMap.size());
    }
}
