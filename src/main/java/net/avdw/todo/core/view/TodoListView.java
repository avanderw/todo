package net.avdw.todo.core.view;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.Mixin;
import net.avdw.todo.repository.Repository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class TodoListView {
    private final Set<Mixin> addonList;
    private final TodoView todoView;

    @Inject
    TodoListView(final Set<Mixin> addonList, final TodoView todoView) {
        this.addonList = addonList;
        this.todoView = todoView;
    }

    public String render(final List<Todo> list, final Repository<Integer, Todo> repository, final int top) {
        String render = "";
        final List<String> preList = addonList.stream().map(addon -> addon.preList(list, repository)).filter(Objects::nonNull).collect(Collectors.toList());
        if (!preList.isEmpty()) {
            render += String.join("\n", preList);
            render += "\n";
        }
        if (top == 0) {
            render += list.stream().map(todoView::render).collect(Collectors.joining("\n"));
        } else {
            render += list.stream().map(todoView::render).limit(top).collect(Collectors.joining("\n"));
        }
        render += "\n";
        render += addonList.stream().map(addon -> addon.postList(list, repository)).filter(Objects::nonNull).collect(Collectors.joining("\n"));
        return render;
    }
}
