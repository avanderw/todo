package net.avdw.todo.core;

import com.google.inject.Inject;
import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.core.mixin.CleanMixin;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.style.StyleApplicator;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TodoListView {
    private final Set<Addon> addonList;
    private final CleanMixin cleanMixin;
    private final StyleApplicator styleApplicator;
    private final TemplatedResource templatedResource;

    @Inject
    TodoListView(final Set<Addon> addonList, final CleanMixin cleanMixin, final StyleApplicator styleApplicator, final TemplatedResource templatedResource) {
        this.addonList = addonList;
        this.cleanMixin = cleanMixin;
        this.styleApplicator = styleApplicator;
        this.templatedResource = templatedResource;
    }

    public String render(final List<Todo> list, final Repository<Integer, Todo> repository) {
        StringBuilder sb = new StringBuilder();

        sb.append(addonList.stream().map(addon -> addon.preRender(list, repository)).filter(Objects::nonNull).collect(Collectors.joining("\n")));
        sb.append("\n");

        sb.append(list.stream().map(todo -> {
            String text = cleanMixin.clean(todo);
            String styledText = styleApplicator.apply(text);
            String escapedText = styledText.replaceAll("\"", "\\\\\"");
            return templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                    String.format("{idx:'%3s',todo:\"%s\"}",
                            todo.getIdx(),
                            escapedText));
        }).collect(Collectors.joining("\n")));
        sb.append("\n");

        sb.append(addonList.stream().map(addon -> addon.postRender(list, repository)).filter(Objects::nonNull).collect(Collectors.joining("\n")));

        return sb.toString();
    }
}
