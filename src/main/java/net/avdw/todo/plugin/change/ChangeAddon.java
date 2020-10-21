package net.avdw.todo.plugin.change;

import com.google.inject.Inject;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.Addon;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ChangeAddon implements Addon {
    private final ChangeMapper changeMapper;
    private final ChangeMixin changeMixin;
    private final PrettyTime prettyTime = new PrettyTime();
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
                .map(changeMapper::mapToLastChangeDate)
                .filter(Optional::isPresent)
                .map(Optional::orElseThrow)
                .max(Comparator.naturalOrder())
                .map(value -> templatedResource.populateKey(ChangeKey.POST_RENDER,
                        String.format("{last:'%s'}", prettyTime.format(value))))
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
            return changeMapper.mapToLastChangeDate(todo)
                    .map(value -> String.format("%17s", prettyTime.format(value)))
                    .orElse(String.format("%17s", "n/a"));
        } else {
            return null;
        }
    }
}
