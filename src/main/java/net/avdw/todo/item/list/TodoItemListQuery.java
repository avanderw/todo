package net.avdw.todo.item.list;

import net.avdw.todo.file.TodoFile;
import net.avdw.todo.priority.Priority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TodoItemListQuery {

    public Optional<Priority> queryHighestFreePriority(final TodoFile todoFile) {
        List<Priority> priorityList = queryFreePriorityList(todoFile);

        if (priorityList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(priorityList.get(0));
        }
    }

    public Optional<Priority> queryLowestFreePriority(final TodoFile todoFile) {
        List<Priority> priorityList = queryFreePriorityList(todoFile);

        if (priorityList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(priorityList.get(priorityList.size() - 1));
        }
    }

    public List<Priority> queryFreePriorityList(final TodoFile todoFile) {
        List<Priority> priorityList = new ArrayList<>(Arrays.asList(Priority.values()));
        todoFile.getTodoItemList().getPriority().stream()
                .map(todoItem -> todoItem.getPriority().get())
                .forEach(priorityList::remove);
        return priorityList;
    }
}
