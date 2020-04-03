package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Working;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.render.TodoContextRenderer;
import net.avdw.todo.render.TodoProjectRenderer;
import net.avdw.todo.theme.Theme;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
@Command(name = "ls", description = "List the items in todo.txt")
public class TodoList implements Runnable {
    @Parameters(description = "Include items that contain these Strings")
    private List<String> filters = new ArrayList<>();

    @Option(names = "--projects", description = "Display projects")
    private boolean displayProjects;
    @Option(names = "--contexts", description = "Display contexts")
    private boolean displayContexts;

    @Option(names = "--not", description = "Exclude items with this String")
    private List<String> notStringList = new ArrayList<>();
    @Option(names = "--and", description = "Include items that contain this String with the filter")
    private List<String> andStringList = new ArrayList<>();
    @Option(names = "--or", description = "Include items that also has this String")
    private List<String> orStringList = new ArrayList<>();

    @Option(names = "--clean", description = "Print todo item without meta tags and index")
    private boolean cleanMeta = false;
    @Option(names = "--greater-than", description = "List items greater than meta:value | yyyy-MM-dd")
    private String greaterThan;
    @Option(names = "--all", description = "Show completed items")
    private boolean showCompleted = false;

    @Inject
    private TodoContextRenderer todoContextRenderer;
    @Inject
    private TodoProjectRenderer todoProjectRenderer;
    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    @Working
    private Path todoPath;
    @Inject
    private Theme theme;
    @Inject
    private SimpleDateFormat simpleDateFormat;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        andStringList.addAll(filters);
        List<TodoItem> todoItemList = todoFileReader.readAll(todoPath);
        List<TodoItem> filteredTodoItemList = new ArrayList<>();
        List<TodoItem> finalFilteredTodoItemList = filteredTodoItemList;
        todoItemList.forEach(item -> {
            String rawValue = item.getRawValue().toLowerCase();
            boolean include = andStringList.isEmpty() || andStringList.stream().map(String::toLowerCase).allMatch(rawValue::contains);
            if (!include && !orStringList.isEmpty()) {
                include = orStringList.stream().map(String::toLowerCase).anyMatch(rawValue::contains);
            }
            if (include && !notStringList.isEmpty()) {
                include = notStringList.stream().map(String::toLowerCase).noneMatch(rawValue::contains);
            }
            if (include && greaterThan != null && !greaterThan.isEmpty()) {
                if (greaterThan.contains(":")) {
                    int indexOf = greaterThan.indexOf(":");
                    String metaKey = greaterThan.substring(0, indexOf);
                    String greaterThanValue = this.greaterThan.substring(indexOf + 1);
                    try {
                        String metaValue = item.getMetaValueFor(metaKey);

                        try {
                            int numValue = Integer.parseInt(metaValue);
                            include = numValue >= Integer.parseInt(greaterThanValue);
                        } catch (RuntimeException e) {
                            include = metaValue.compareTo(greaterThanValue) >= 0;
                        }
                    } catch (RuntimeException e) {
                        include = false;
                    }
                } else {
                    try {
                        Date greaterThan = simpleDateFormat.parse(this.greaterThan);
                        Date createdDate = item.getCreatedDate();
                        include = createdDate.after(greaterThan) || createdDate.equals(greaterThan);
                    } catch (RuntimeException | ParseException e) {
                        throw new UnsupportedOperationException();
                    }
                }
            }
            if (include) {
                finalFilteredTodoItemList.add(item);
            }
        });

        if (!showCompleted) {
            filteredTodoItemList = filteredTodoItemList.stream().filter(TodoItem::isIncomplete).collect(Collectors.toList());
        }

        if (filteredTodoItemList.isEmpty()) {
            Logger.info("The list is empty");
        }

        if (displayContexts) {
            todoContextRenderer.printContextTable(filteredTodoItemList);
        }
        if (displayProjects) {
            todoProjectRenderer.printProjectTable(filteredTodoItemList);
        }

        theme.printHeader("list");
        if (cleanMeta) {
            filteredTodoItemList.forEach(theme::printCleanTodoItemWithoutIdx);
        } else {
            filteredTodoItemList.forEach(theme::printFullTodoItemWithIdx);
        }
        theme.printDuration();
        theme.printDisplaySummary(filteredTodoItemList.size(), todoItemList.size());
    }
}
