package net.avdw.todo.item;

import com.google.inject.Inject;

public class TodoItemCleaner {
    private TodoItemFactory todoItemFactory;

    @Inject
    TodoItemCleaner(final TodoItemFactory todoItemFactory) {
        this.todoItemFactory = todoItemFactory;
    }

    public TodoItem clean(final TodoItem todoItem) {
        String cleanTodoItem = todoItem.getRawValue();
        cleanTodoItem = cleanTodoItem.replaceAll("\\s\\S*:\\S*", "");
        cleanTodoItem = cleanTodoItem.replaceFirst("^x \\d\\d\\d\\d-\\d\\d-\\d\\d\\s", "");
        cleanTodoItem = cleanTodoItem.replaceFirst("^\\d\\d\\d\\d-\\d\\d-\\d\\d\\s", "");
        cleanTodoItem = cleanTodoItem.replaceFirst("^\\([A-Z]\\) \\d\\d\\d\\d-\\d\\d-\\d\\d\\s", "");
        return todoItemFactory.create(todoItem.getIdx(), cleanTodoItem);
    }
}
