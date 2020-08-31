package net.avdw.todo.item.list;

import net.avdw.todo.file.TodoFile;
import net.avdw.todo.item.TodoItem;
import org.tinylog.Logger;

import java.util.List;
import java.util.stream.Collectors;

@Deprecated
public class TodoItemListFilter {
    public List<TodoItem> filterByIdx(final TodoFile todoFile, final List<Integer> idxList) {
        return todoFile.getTodoItemList().getAll().stream().filter(todoItem -> idxList.contains(todoItem.getIdx())).collect(Collectors.toList());
    }

    public List<TodoItem> filterPriorityItems(final List<TodoItem> todoItemList) {
        Logger.debug(String.format("Filtering priority items from '%s' todo items", todoItemList.size()));
        List<TodoItem> filteredTodoItems = todoItemList.stream()
                .filter(TodoItem::hasPriority)
                .collect(Collectors.toList());
        Logger.debug(String.format("Filtered list contains '%s' todo items", filteredTodoItems.size()));
        return filteredTodoItems;
    }

    public List<TodoItem> filterInProgressTodoItems(final List<TodoItem> todoItemList) {
        Logger.debug(String.format("Filtering in-progress items from '%s' todo items", todoItemList.size()));
        List<TodoItem> filteredTodoItems = todoItemList.stream()
                .filter(TodoItem::isInProgress)
                .collect(Collectors.toList());
        Logger.debug(String.format("Filtered list contains '%s' todo items", filteredTodoItems.size()));
        return filteredTodoItems;
    }

    public List<TodoItem> filterIncompleteItems(final List<TodoItem> todoItemList) {
        Logger.debug(String.format("Filtering done items from '%s' todo items", todoItemList.size()));
        List<TodoItem> filteredTodoItems = todoItemList.stream()
                .filter(TodoItem::isIncomplete)
                .collect(Collectors.toList());
        Logger.debug(String.format("Filtered list contains '%s' todo items", filteredTodoItems.size()));
        return filteredTodoItems;
    }

}
