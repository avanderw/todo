package net.avdw.todo.extension;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;

import java.util.List;

public interface Mixin {
    String postList(final List<Todo> list, final Repository<Integer, Todo> repository);

    String postTodo(final Todo todo);

    String preList(final List<Todo> list, final Repository<Integer, Todo> repository);

    String preTodo(final Todo todo);
}
