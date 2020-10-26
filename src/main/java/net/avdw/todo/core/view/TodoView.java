package net.avdw.todo.core.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.plugin.Addon;
import net.avdw.todo.style.TodoStyler;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class TodoView {
    private final Set<Addon> addonList;
    private final TemplatedResource templatedResource;
    private final TodoStyler todoStyler;

    @Inject
    public TodoView(final Set<Addon> addonList, final TodoStyler todoStyler, final TemplatedResource templatedResource) {
        this.addonList = addonList;
        this.todoStyler = todoStyler;
        this.templatedResource = templatedResource;
    }

    public String render(final Todo todo) {
        String styledText = todoStyler.style(todo);
        String escapedText = styledText.replaceAll("\"", "\\\\\"");
        return templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                String.format("{idx:'%3s',pre:'%s',todo:\"%s\",post:'%s'}",
                        todo.getIdx(),
                        addonList.stream().map(addon -> addon.preTodo(todo)).filter(Objects::nonNull).collect(Collectors.joining(" ")),
                        escapedText,
                        addonList.stream().map(addon -> addon.postTodo(todo)).filter(Objects::nonNull).collect(Collectors.joining(" "))
                ));
    }
}
