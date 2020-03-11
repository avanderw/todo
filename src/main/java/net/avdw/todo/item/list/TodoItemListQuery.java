package net.avdw.todo.item.list;

import net.avdw.todo.action.TodoPriority;
import net.avdw.todo.file.TodoFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TodoItemListQuery {

    public Optional<TodoPriority.Priority> queryHighestFreePriority(final TodoFile todoFile) {
        List<TodoPriority.Priority> priorityList = queryFreePriorityList(todoFile);

        if (priorityList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(priorityList.get(0));
        }
    }

    public Optional<TodoPriority.Priority> queryLowestFreePriority(final TodoFile todoFile) {
        List<TodoPriority.Priority> priorityList = queryFreePriorityList(todoFile);

        if (priorityList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(priorityList.get(priorityList.size() - 1));
        }
    }

    public List<TodoPriority.Priority> queryFreePriorityList(final TodoFile todoFile) {
        List<TodoPriority.Priority> priorityList = new ArrayList<>(Arrays.asList(TodoPriority.Priority.values()));
        todoFile.getTodoItemList().getPriority().stream()
                .map(todoItem -> todoItem.getPriority().get())
                .forEach(priorityList::remove);
        return priorityList;
    }
}
