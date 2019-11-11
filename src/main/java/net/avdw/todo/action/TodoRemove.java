package net.avdw.todo.action;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.theme.ThemeApplicator;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.StringWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Command(name = "rm", description = "Remove a todo item")
public class TodoRemove implements Runnable {
    @ParentCommand
    private Todo todo;
    @Parameters(description = "Index to remove", arity = "1")
    private int idx;
    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    private TodoFileWriter todoFileWriter;
    @Inject
    private ThemeApplicator themeApplicator;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        remove(todo.getTodoFile(), idx);
    }

    /**
     * Remove a line from a text file.
     * The index to delete is relative to what is displayed.
     *
     * @param fromFile the file to remove the line index of
     * @param idx      the todo index to find
     * @return the todo entry that was removed
     */
    public Optional<TodoItem> remove(final Path fromFile, final int idx) {
        List<TodoItem> allTodoItems = todoFileReader.readAll(fromFile);
        if (idx > allTodoItems.size()) {
            Logger.warn(String.format("There are only '%s' items in the todo file and idx '%s' is too high", allTodoItems.size(), idx));
            return Optional.empty();
        } else if (idx <= 0) {
            Logger.warn(String.format("The idx '%s' cannot be negative", idx));
            return Optional.empty();
        }

        TodoItem todoItem = allTodoItems.get(idx - 1);
        allTodoItems.remove(idx - 1);
        todoFileWriter.write(allTodoItems, fromFile);

        RemoveModel model = new RemoveModel();
        Map<String, Object> context = new HashMap<>();
        context.put("theme", themeApplicator);
        context.put("model", model);

        model.removedItem = todoItem;
        model.completeItems = allTodoItems.stream().filter(TodoItem::isComplete).collect(Collectors.toList());
        model.incompleteItems = allTodoItems.stream().filter(TodoItem::isIncomplete).collect(Collectors.toList());
        model.todoFilePath = fromFile;
        model.completion = model.completeItems.size() * 100 / allTodoItems.size();

        Mustache m = new DefaultMustacheFactory().compile("todo-remove.mustache");
        StringWriter writer = new StringWriter();
        m.execute(writer, context);
        System.out.println(writer.toString());

        return Optional.of(todoItem);
    }


    static class RemoveModel {
        private int completion;
        private Path todoFilePath;
        private List<TodoItem> completeItems;
        private List<TodoItem> incompleteItems;
        private TodoItem removedItem;

        public TodoItem getRemovedItem() {
            return removedItem;
        }

        public List<TodoItem> getCompleteItems() {
            return completeItems;
        }

        public List<TodoItem> getIncompleteItems() {
            return incompleteItems;
        }

        public int getCompletion() {
            return completion;
        }

        public Path getTodoFilePath() {
            return todoFilePath;
        }
    }
}
