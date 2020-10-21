package net.avdw.todo.core;

import com.google.inject.Inject;
import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.mixin.CleanMixin;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.style.StyleApplicator;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TodoView {
    private final Set<Addon> addonList;
    private final CleanMixin cleanMixin;
    private final StyleApplicator styleApplicator;
    private final TemplatedResource templatedResource;

    @Inject
    public TodoView(final Set<Addon> addonList, final CleanMixin cleanMixin, final StyleApplicator styleApplicator, final TemplatedResource templatedResource) {
        this.addonList = addonList;
        this.cleanMixin = cleanMixin;
        this.styleApplicator = styleApplicator;
        this.templatedResource = templatedResource;
    }

    public String render(final Todo todo) {
        String text = cleanMixin.clean(todo);
        String styledText = styleApplicator.apply(text);
        String escapedText = styledText.replaceAll("\"", "\\\\\"");
        return templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                String.format("{idx:'%3s',pre:'%s',todo:\"%s\",post:'%s'}",
                        todo.getIdx(),
                        addonList.stream().map(addon -> addon.preTodo(todo)).filter(Objects::nonNull).collect(Collectors.joining("\n")),
                        escapedText,
                        addonList.stream().map(addon -> addon.postTodo(todo)).filter(Objects::nonNull).collect(Collectors.joining("\n"))
                ));
    }
}
