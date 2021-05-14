package net.avdw.todo.extension.change;

import net.avdw.todo.TemplatedResource;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.Mixin;
import net.avdw.todo.repository.Repository;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ChangeAddon implements Mixin {
    private final ChangeMapper changeMapper;
    private final ChangeMixin changeMixin;
    private final TemplatedResource templatedResource;

    @Inject
    ChangeAddon(final ChangeMapper changeMapper, final ChangeMixin changeMixin, final TemplatedResource templatedResource) {
        this.changeMapper = changeMapper;
        this.changeMixin = changeMixin;
        this.templatedResource = templatedResource;
    }

    @Override
    public String postList(final List<Todo> list, final Repository<Integer, Todo> repository) {
        return list.stream()
                .map(changeMapper::mapToChange)
                .map(Change::getDate)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .map(value -> templatedResource.populateKey(ChangeKey.POST_RENDER,
                        String.format("{last:'%s'}", value)))
                .orElse(null);
    }

    @Override
    public String postTodo(final Todo todo) {
        return null;
    }

    @Override
    public String preList(final List<Todo> list, final Repository<Integer, Todo> repository) {
        return null;
    }

    @Override
    public String preTodo(final Todo todo) {
        if (changeMixin.showDetail) {
            final Change change = changeMapper.mapToChange(todo);
            return change.getDate() == null ? String.format("%17s", "n/a") : String.format("%17s", change.getDate());
        } else {
            return null;
        }
    }
}
