package net.avdw.todo.core.view;

import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.style.TodoStyler;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.Mixin;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class TodoView {
    private final Set<Mixin> addonList;
    private final TemplatedResource templatedResource;
    private final TodoStyler todoStyler;

    @Inject
    public TodoView(final Set<Mixin> addonList, final TodoStyler todoStyler, final TemplatedResource templatedResource) {
        this.addonList = addonList;
        this.todoStyler = todoStyler;
        this.templatedResource = templatedResource;
    }

    public String render(final Todo todo) {
        final String styledText = todoStyler.style(todo);
        final String escapedText = styledText.replaceAll("\"", "\\\\\"");
        return templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                String.format("{idx:'%3s',pre:'%s',todo:\"%s\",post:'%s'}",
                        todo.getIdx(),
                        addonList.stream().map(addon -> addon.preTodo(todo)).filter(Objects::nonNull).collect(Collectors.joining(" ")),
                        escapedText,
                        addonList.stream().map(addon -> addon.postTodo(todo)).filter(Objects::nonNull).collect(Collectors.joining(" "))
                ));
    }
}
