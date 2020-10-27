package net.avdw.todo.plugin.state;

import com.google.inject.Inject;
import net.avdw.todo.core.groupby.Group;
import net.avdw.todo.domain.Todo;

import java.util.Locale;
import java.util.function.Function;

public class StateGroup implements Group<Todo, String> {
    private final StateMapper stateMapper;

    @Inject
    public StateGroup(final StateMapper stateMapper) {
        this.stateMapper = stateMapper;
    }

    @Override
    public Function<Todo, String> collector() {
        return (stateMapper::map);
    }

    @Override
    public boolean isSatisfiedBy(final String selector) {
        return selector.toLowerCase(Locale.ENGLISH).equals("state");
    }

    @Override
    public String name() {
        return "";
    }
}
