package net.avdw.todo.item;

import com.google.inject.Inject;
import org.pmw.tinylog.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TodoItemCompletor {

    private final SimpleDateFormat simpleDateFormat;

    @Inject
    TodoItemCompletor(final SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
    }

    /**
     * Create a new todo item from an existing todo item that is complete.
     *
     * @param todoItem the todo item to complete
     * @return a new instance of the completed todo item, otherwise the original item if complete
     */
    public TodoItem complete(final TodoItem todoItem) {
        if (todoItem.isComplete()) {
            Logger.info("The todo item is already complete");
            return todoItem;
        }

        String completedRawValue = String.format("x %s %s",
                simpleDateFormat.format(new Date()),
                todoItem.rawValue().replaceFirst("\\([A-Z]\\) ", ""));
        return new TodoItem(todoItem.getIdx(), completedRawValue);
    }
}
