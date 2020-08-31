package net.avdw.todo.item;

import com.google.inject.Inject;
import net.avdw.todo.Priority;
import org.tinylog.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

@Deprecated
public class TodoItemModifier {
    private final TodoItemFactory todoItemFactory;
    private final SimpleDateFormat simpleDateFormat;

    @Inject
    TodoItemModifier(final TodoItemFactory todoItemFactory, final SimpleDateFormat simpleDateFormat) {
        this.todoItemFactory = todoItemFactory;
        this.simpleDateFormat = simpleDateFormat;
    }

    public TodoItem stripCompletionDate(final TodoItem todoItem) {
        return todoItemFactory.create(todoItem.getIdx(), todoItem.getRawValue().replaceFirst("^x \\d\\d\\d\\d-\\d\\d-\\d\\d\\s", ""));
    }

    public TodoItem stripPriority(final TodoItem todoItem) {
        return todoItemFactory.create(todoItem.getIdx(), todoItem.getRawValue().replaceFirst("^\\([A-Z]\\)\\s", ""));
    }

    public TodoItem stripStartDate(final TodoItem todoItem) {
        return todoItemFactory.create(todoItem.getIdx(), todoItem.getRawValue().replaceFirst("^\\d\\d\\d\\d-\\d\\d-\\d\\d\\s", ""));
    }

    public TodoItem changeDueDate(final TodoItem todoItem, final Date dueDate) {
        return todoItemFactory.create(todoItem.getIdx(), todoItem.getRawValue()
                .replaceAll("due:\\d\\d\\d\\d-\\d\\d-\\d\\d", String.format("due:%s", simpleDateFormat.format(dueDate))));
    }

    public TodoItem addStartDate(final TodoItem todoItem) {
        return todoItemFactory.create(todoItem.getIdx(), String.format("%s %s", simpleDateFormat.format(new Date()), todoItem.getRawValue()));
    }

    public TodoItem complete(final TodoItem todoItem) {
        if (todoItem.isComplete()) {
            Logger.debug("Item is already completed [{}] {}", todoItem.getIdx(), todoItem);
            return todoItem;
        }
        return todoItemFactory.create(todoItem.getIdx(), String.format("x %s %s", simpleDateFormat.format(new Date()), todoItem.getRawValue()));
    }

    public TodoItem changeIdx(final TodoItem modifiedTodoItem, final int idx) {
        return todoItemFactory.create(idx, modifiedTodoItem.getRawValue());
    }

    public TodoItem changePriority(final TodoItem todoItem, final Priority priority) {
        return todoItemFactory.create(todoItem.getIdx(), String.format("(%s) %s", priority.name(), stripPriority(todoItem).getRawValue()));

    }
}
