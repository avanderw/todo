package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.Ansi;
import net.avdw.todo.GlobalTodo;
import net.avdw.todo.LocalTodo;
import net.avdw.todo.Todo;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.property.GlobalProperty;
import net.avdw.todo.property.PropertyModule;
import net.avdw.todo.render.TodoContextTable;
import net.avdw.todo.render.TodoDoneStatusbar;
import net.avdw.todo.render.TodoProjectTable;
import org.pmw.tinylog.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static net.avdw.todo.render.ConsoleFormatting.*;

@Command(name = "status", description = "Display repository information")
public class TodoStatus implements Runnable {

    @ParentCommand
    private Todo todo;

    @Inject
    @GlobalTodo
    private Path globalPath;

    @Inject
    @LocalTodo
    private Path localPath;

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
    private TodoProjectTable todoProjectTable;

    @Inject
    private TodoContextTable todoContextTable;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        h1("todo:status");
        Logger.info(String.format("Local   : %s", Files.exists(localPath) ? localPath.toAbsolutePath() : "todo init"));
        Logger.info(String.format("Global  : %s", Files.exists(globalPath) ? globalPath.toAbsolutePath() : "todo --global init"));
        Path defaultPath = todo.findDirectory();
        Logger.info(String.format("Default : %s%s%s", Ansi.YELLOW, defaultPath.toAbsolutePath(), Ansi.RESET));

        h2("status:paths");
        if (properties.containsKey(PropertyModule.TODO_PATHS)) {
            String todoPaths = properties.getProperty(PropertyModule.TODO_PATHS);
            List<String> pathsToRemove = new ArrayList<>();
            Arrays.stream(todoPaths.split(";")).forEach(path -> {
                try {
                    Path currentPath = Paths.get(path);
                    List<TodoItem> allTodoItemList = todoFileReader.readAll(currentPath.resolve("todo.txt"));
                    if (defaultPath.toAbsolutePath().equals(currentPath.toAbsolutePath())) {
                        Logger.info(String.format("%s%s%s", Ansi.YELLOW, path, Ansi.RESET));
                    } else {
                        Logger.info(String.format("%s", path));
                    }
                    Logger.info(String.format("Progress: %s", todoDoneStatusbar.createPercentageBar(allTodoItemList)));

                    todoProjectTable.printProjectSummaryTable(allTodoItemList);
                    todoContextTable.printContextSummaryTable(allTodoItemList);
                } catch (Exception e) {
                    Logger.error(String.format("Cannot read path '%s'", path));
                    Logger.info("Removing path from property file");
                    pathsToRemove.add(path);
                }
                hr();
            });

            if (!pathsToRemove.isEmpty()) {
                for (String path : pathsToRemove) {
                    todoPaths = todoPaths.replace(path, "");
                    todoPaths = todoPaths.replaceAll(";;", ";");
                }
                properties.setProperty(PropertyModule.TODO_PATHS, todoPaths);
                try {
                    properties.store(new FileWriter(propertyPath.toFile()), "Todo Properties");
                } catch (IOException e) {
                    Logger.error(e.getMessage());
                    Logger.debug(e);
                }
                Logger.info("Wrote new property file");
            }
        } else {
            Logger.info("No paths found");
        }

        if (!Files.exists(localPath) || !Files.exists(globalPath)) {
            hr();
            CommandLine.usage(TodoInit.class, System.out);
        }
    }
}
