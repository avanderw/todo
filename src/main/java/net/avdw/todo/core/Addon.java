package net.avdw.todo.core;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;

import java.util.List;

public interface Addon {
    String postList(final List<Todo> list, final Repository<Integer, Todo> repository);

    String postTodo(final Todo todo);

    String preList(final List<Todo> list, final Repository<Integer, Todo> repository);

    String preTodo(final Todo todo);
}
