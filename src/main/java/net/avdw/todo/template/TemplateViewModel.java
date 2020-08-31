package net.avdw.todo.template;

import net.avdw.todo.file.TodoFile;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.list.TodoItemList;

import java.util.List;

@Deprecated
public class TemplateViewModel {
    private final String view;
    private final TodoItemList todoItemList;
    private final TodoFile fileAfter;
    private final TodoFile fileBefore;

    public TemplateViewModel(final String view, final List<TodoItem> todoItemList, final TodoFile fileBefore, final TodoFile fileAfter) {
        this.view = view;
        this.todoItemList = new TodoItemList(todoItemList);
        this.fileBefore = fileBefore;
        this.fileAfter = fileAfter;
    }

    public String getView() {
        return view;
    }

    public TodoItemList getTodoItemList() {
        return todoItemList;
    }

    public TodoFile getFileAfter() {
        return fileAfter;
    }

    public TodoFile getFileBefore() {
        return fileBefore;
    }

}
