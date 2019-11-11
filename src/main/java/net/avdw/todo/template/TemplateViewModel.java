package net.avdw.todo.template;

import net.avdw.todo.file.TodoFile;
import net.avdw.todo.item.TodoItem;

import java.util.List;
import java.util.stream.Collectors;

public class TemplateViewModel {
    private final String view;
    private List<TodoItem> filteredTodoItems;
    private List<TodoItem> allTodoItems;
    private List<TodoItem> completedTodoItems;
    private TodoFile workingFile;

    public TemplateViewModel(final String view) {
        this.view = view;
    }

    public String getView() {
        return view;
    }

    public List<TodoItem> getFilteredTodoItems() {
        return filteredTodoItems;
    }

    public void setFilteredTodoItems(final List<TodoItem> filteredTodoItems) {
        this.filteredTodoItems = filteredTodoItems;
    }

    public void setAllTodoItems(final List<TodoItem> allTodoItems) {
        this.allTodoItems = allTodoItems;
        completedTodoItems = allTodoItems.stream().filter(TodoItem::isComplete).collect(Collectors.toList());
    }

    public List<TodoItem> getAllTodoItems() {
        return allTodoItems;
    }

    public List<TodoItem> getCompletedTodoItems() {
        return completedTodoItems;
    }

    public TodoFile getWorkingFile() {
        return workingFile;
    }

    public void setWorkingFile(final TodoFile workingFile) {
        this.workingFile = workingFile;
    }
}
