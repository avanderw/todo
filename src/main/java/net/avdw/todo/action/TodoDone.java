package net.avdw.todo.action;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.file.TodoFileWriter;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.TodoItemCompletor;
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

@Command(name = "do", description = "Complete a todo item")
public class TodoDone implements Runnable {
    @ParentCommand
    private Todo todo;

    @Parameters(description = "Index to complete", arity = "1")
    private int idx;

    @Inject
    private TodoFileReader todoFileReader;

    @Inject
    private TodoFileWriter todoFileWriter;

    @Inject
    private TodoItemCompletor todoItemCompletor;
    @Inject
    private ThemeApplicator themeApplicator;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        complete(todo.getTodoFile(), idx);
    }

    /**
     * Find and mark an entry in a file at idx as done.
     *
     * @param todoFile the file to search
     * @param idx      the entry in the file to complete
     * @return the original line that was marked as done
     */
    void complete(final Path todoFile, final int idx) {
        List<TodoItem> allTodoItems = todoFileReader.readAll(todoFile);
        if (idx > allTodoItems.size()) {
            Logger.warn(String.format("There are only '%s' items in the todo file and idx '%s' is too high", allTodoItems.size(), idx));
            return;
        } else if (idx <= 0) {
            Logger.warn(String.format("The idx '%s' cannot be negative", idx));
            return;
        }

        TodoItem todoItem = allTodoItems.get(idx - 1);
        if (todoItem.isComplete()) {
            Logger.warn("Item is already marked as done");
        } else {
            TodoItem completeItem = todoItemCompletor.complete(todoItem);

            allTodoItems.set(idx - 1, completeItem);
            todoFileWriter.write(allTodoItems, todoFile);

            DoneModel model = new DoneModel();
            Map<String, Object> context = new HashMap<>();
            context.put("theme", themeApplicator);
            context.put("model", model);

            model.foundItem = todoItem;
            model.doneItem = completeItem;

            Mustache m = new DefaultMustacheFactory().compile("todo-done.mustache");
            StringWriter writer = new StringWriter();
            m.execute(writer, context);
            System.out.println(writer.toString());
        }
    }

    static class DoneModel {

        private TodoItem doneItem;
        private TodoItem foundItem;

        public TodoItem getDoneItem() {
            return doneItem;
        }

        public TodoItem getFoundItem() {
            return foundItem;
        }
    }
}
