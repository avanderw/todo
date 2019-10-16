package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.property.GlobalProperty;
import net.avdw.todo.property.PropertyModule;
import net.avdw.todo.render.TodoContextRenderer;
import net.avdw.todo.render.TodoDoneStatusbar;
import net.avdw.todo.render.TodoProjectRenderer;
import net.avdw.todo.theme.ThemeApplicator;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Command(name = "status", description = "Display repository information")
public class TodoStatus implements Runnable {

    @ParentCommand
    private Todo todo;

    @Inject
    @GlobalProperty
    private Properties properties;
    @Inject
    @GlobalProperty
    private Path propertyPath;

    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    private TodoDoneStatusbar todoDoneStatusbar;
    @Inject
    private TodoProjectRenderer projectRenderer;
    @Inject
    private TodoContextRenderer contextRenderer;
    @Inject
    private ThemeApplicator themeApplicator;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        System.out.println(themeApplicator.h1("todo:status"));
        Path resolvedPath = todo.resolveTodoPath().toAbsolutePath();
        properties.computeIfPresent(PropertyModule.TODO_PATHS, (key, value) -> {
            String paths = (String) value;
            List<String> pathsToRemove = new ArrayList<>();
            Arrays.stream(paths.split(";")).forEach(path -> {
                try {
                    Path currentPath = Paths.get(path);
                    List<TodoItem> allTodoItemList = todoFileReader.readAll(currentPath.resolve("todo.txt"));
                    long incomplete = allTodoItemList.stream().filter(TodoItem::isIncomplete).count();
                    long complete = allTodoItemList.stream().filter(TodoItem::isComplete).count();
                    String bar = String.format("[%3s/%-3s] %s", complete, allTodoItemList.size(), todoDoneStatusbar.createPercentageBar(allTodoItemList));
                    if (currentPath.equals(resolvedPath)) {
                        System.out.println(String.format("[%3s] %s %s", incomplete, bar, themeApplicator.selected(path)));
                    } else {
                        System.out.println(String.format("[%3s] %s %s", incomplete, bar, themeApplicator.txt(path)));
                    }
                    System.out.println(String.format("%s, %s",
                            projectRenderer.renderOneLineSummary(allTodoItemList),
                            contextRenderer.renderOneLineSummary(allTodoItemList)));
                    if (currentPath.equals(resolvedPath)) {
                        projectRenderer.renderSummaryTable(allTodoItemList);
                        contextRenderer.renderSummaryTable(allTodoItemList);
                    }
                    System.out.println(themeApplicator.hr());
                } catch (Exception e) {
                    Logger.error(String.format("Cannot read path '%s'", path));
                    Logger.info("Removing path from property file");
                    pathsToRemove.add(path);
                }
            });

            if (!pathsToRemove.isEmpty()) {
                for (String path : pathsToRemove) {
                    paths = paths.replace(path, "");
                    paths = paths.replaceAll(";;", ";");
                }
                properties.setProperty(PropertyModule.TODO_PATHS, paths);
                try {
                    properties.store(new FileWriter(propertyPath.toFile()), "Todo Properties");
                } catch (IOException e) {
                    Logger.error(e.getMessage());
                    Logger.debug(e);
                }
                Logger.info("Wrote new property file");
            }
            return value;
        });
    }
}
