package net.avdw.todo.admin;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.inject.Inject;
import com.google.inject.Provider;
import net.avdw.todo.Todo;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.property.GlobalProperty;
import net.avdw.todo.property.PropertyModule;
import net.avdw.todo.render.TodoBarRenderer;
import net.avdw.todo.theme.ThemeApplicator;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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
    private ThemeApplicator themeApplicator;
    @Inject
    private TodoFileModelProvider todoFileModelProvider;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        StatusModel model = new StatusModel();
        Map<String, Object> context = new HashMap<>();
        context.put("theme", themeApplicator);
        context.put("model", model);

        Path resolvedPath = todo.resolveTodoPath().toAbsolutePath();
        properties.computeIfPresent(PropertyModule.TODO_PATHS, (key, value) -> {
            String paths = (String) value;
            List<String> pathsToRemove = new ArrayList<>();
            Arrays.stream(paths.split(";")).forEach(path -> {
                try {
                    Path currentPath = Paths.get(path);
                    StatusFileModel statusFileModel = todoFileModelProvider.get();
                    statusFileModel.setPath(currentPath);
                    statusFileModel.setTodoItemList(todoFileReader.readAll(currentPath.resolve("todo.txt")));

                    if (currentPath.equals(resolvedPath)) {
                        model.selected = statusFileModel;
                        statusFileModel.selected = true;
                    }
                    model.knownPathList.add(statusFileModel);
                } catch (Exception e) {
                    Logger.error(String.format("Cannot read path '%s'", path));
                    Logger.debug(e);
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

        Mustache m = new DefaultMustacheFactory().compile("todo-status.mustache");
        StringWriter writer = new StringWriter();
        m.execute(writer, context);
        System.out.println(writer.toString());
    }

    static class StatusModel {
        private final List<StatusFileModel> knownPathList = new ArrayList<>();
        private StatusFileModel selected;

        public List<StatusFileModel> getKnownPathList() {
            return knownPathList;
        }

        public StatusFileModel getSelected() {
            return selected;
        }
    }

    static class StatusFileModel {

        private boolean selected = false;
        private Path path;
        private List<TodoItem> todoItemList = new ArrayList<>();
        private final TodoBarRenderer todoBarRenderer;
        private final ThemeApplicator themeApplicator;

        @Inject
        StatusFileModel(final TodoBarRenderer todoBarRenderer, final ThemeApplicator themeApplicator) {
            this.todoBarRenderer = todoBarRenderer;
            this.themeApplicator = themeApplicator;
        }

        public Path getPath() {
            return path;
        }

        public void setPath(final Path path) {
            this.path = path;
        }

        public List<TodoItem> getTodoItemList() {
            return todoItemList;
        }

        public void setTodoItemList(final List<TodoItem> todoItemList) {
            this.todoItemList = todoItemList;
        }

        public List<TodoItem> getIncompleteItems() {
            return todoItemList.stream().filter(TodoItem::isIncomplete).collect(Collectors.toList());
        }

        public List<TodoItem> getCompleteItems() {
            return todoItemList.stream().filter(TodoItem::isComplete).collect(Collectors.toList());
        }

        public double getProgress() {
            return (double) getCompleteItems().size() / todoItemList.size();
        }


        private Map<String, List<TodoItem>> getProjects() {
            Map<String, List<TodoItem>> projects = new HashMap<>();
            todoItemList.forEach(todoItem -> todoItem.getProjects().forEach(project -> {
                projects.putIfAbsent(project, new ArrayList<>());
                projects.get(project).add(todoItem);
            }));
            return projects;
        }

        private Map<String, List<TodoItem>> getContexts() {
            Map<String, List<TodoItem>> contexts = new HashMap<>();
            todoItemList.forEach(todoItem -> todoItem.getContexts().forEach(context -> {
                contexts.putIfAbsent(context, new ArrayList<>());
                contexts.get(context).add(todoItem);
            }));
            return contexts;
        }

        public long getProjectCount() {
            return getProjects().size();
        }

        public long getContextCount() {
            return getContexts().size();
        }

        private List<String> formatTable(final Map<String, List<TodoItem>> items) {
            List<String> formatted = new ArrayList<>();
            int count = 0;
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, List<TodoItem>> entry : items.entrySet().stream()
                    .sorted(Comparator.comparing(contextListEntry -> {
                        double completed = contextListEntry.getValue().stream().filter(TodoItem::isComplete).count();
                        return completed / contextListEntry.getValue().size();
                    }))
                    .collect(Collectors.toList())) {

                if (count++ % 4 == 0 && stringBuilder.length() != 0) {
                    formatted.add(stringBuilder.toString());
                    stringBuilder.delete(0, stringBuilder.length());
                }

                double completed = entry.getValue().stream().filter(TodoItem::isComplete).count();
                double progress = completed / entry.getValue().size();
                String percentage = String.format("%3.0f%%", progress * 100);
                stringBuilder.append(themeApplicator.progress(String.format("%15s [%2s] %s", entry.getKey(), entry.getValue().size(), percentage), 1 - progress));
            }
            formatted.add(stringBuilder.toString());
            return formatted;
        }

        public List<String> getFormattedContexts() {
            return formatTable(getContexts());
        }

        public List<String> getFormattedProjects() {
            return formatTable(getProjects());
        }

        public boolean isSelected() {
            return selected;
        }
    }

    static class TodoFileModelProvider implements Provider<StatusFileModel> {
        private final TodoBarRenderer todoBarRenderer;
        private final ThemeApplicator themeApplicator;

        @Inject
        TodoFileModelProvider(final TodoBarRenderer todoBarRenderer, final ThemeApplicator themeApplicator) {
            this.todoBarRenderer = todoBarRenderer;
            this.themeApplicator = themeApplicator;
        }

        @Override
        public StatusFileModel get() {
            return new StatusFileModel(todoBarRenderer, themeApplicator);
        }
    }

}
