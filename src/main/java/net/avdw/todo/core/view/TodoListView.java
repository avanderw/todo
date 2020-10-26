package net.avdw.todo.core.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.plugin.Addon;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class TodoListView {
    private final Set<Addon> addonList;
    private final TodoView todoView;

    @Inject
    TodoListView(final Set<Addon> addonList, final TodoView todoView) {
        this.addonList = addonList;
        this.todoView = todoView;
    }

    public String render(final List<Todo> list, final Repository<Integer, Todo> repository) {
        String render = "";
        List<String> preList = addonList.stream().map(addon -> addon.preList(list, repository)).filter(Objects::nonNull).collect(Collectors.toList());
        if (!preList.isEmpty()) {
            render += String.join("\n", preList);
            render += "\n";
        }
        render += list.stream().map(todoView::render).collect(Collectors.joining("\n"));
        render += "\n";
        render += addonList.stream().map(addon -> addon.postList(list, repository)).filter(Objects::nonNull).collect(Collectors.joining("\n"));
        return render;
    }
}
