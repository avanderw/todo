package net.avdw.todo.core;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TodoListView {
    private final Set<Addon> addonList;
    private final TodoView todoView;

    @Inject
    TodoListView(final Set<Addon> addonList, final TodoView todoView) {
        this.addonList = addonList;
        this.todoView = todoView;
    }

    public String render(final List<Todo> list, final Repository<Integer, Todo> repository) {
        return addonList.stream().map(addon -> addon.preList(list, repository)).filter(Objects::nonNull).collect(Collectors.joining("\n")) +
                "\n" +
                list.stream().map(todoView::render).collect(Collectors.joining("\n")) +
                "\n" +
                addonList.stream().map(addon -> addon.postList(list, repository)).filter(Objects::nonNull).collect(Collectors.joining("\n"));
    }
}
