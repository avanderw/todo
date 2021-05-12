package net.avdw.todo.extension.change;

import net.avdw.todo.core.groupby.Group;
import net.avdw.todo.domain.Todo;

import javax.inject.Inject;
import java.util.Locale;
import java.util.function.Function;

public class ChangeTypeGroup implements Group<Todo, String> {
    private final ChangeMapper changeMapper;

    @Inject
    public ChangeTypeGroup(final ChangeMapper changeMapper) {
        this.changeMapper = changeMapper;
    }

    @Override
    public Function<Todo, String> collector() {
        return todo -> changeMapper.mapToChange(todo).getType();
    }

    @Override
    public boolean isSatisfiedBy(final String selector) {
        return selector.toLowerCase(Locale.ENGLISH).equals("change");
    }

    @Override
    public String name() {
        return "";
    }
}
