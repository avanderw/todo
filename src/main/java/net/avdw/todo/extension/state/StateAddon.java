package net.avdw.todo.extension.state;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.Mixin;
import net.avdw.todo.repository.Repository;

import java.util.List;

@Singleton
public class StateAddon implements Mixin {
    private final StateMapper stateMapper;
    private final StateMixin stateMixin;

    @Inject
    StateAddon(final StateMixin stateMixin, final StateMapper stateMapper) {
        this.stateMixin = stateMixin;
        this.stateMapper = stateMapper;
    }

    @Override
    public String postList(final List<Todo> list, final Repository<Integer, Todo> repository) {
        return null;
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
        if (stateMixin.showState) {
            return String.format("%" + stateMapper.maxWidth() + "s", stateMapper.map(todo));
        } else {
            return null;
        }
    }
}
