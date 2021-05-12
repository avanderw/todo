package net.avdw.todo.extension;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;

import java.util.List;

public interface Mixin {
    String postList(List<Todo> list, Repository<Integer, Todo> repository);

    String postTodo(Todo todo);

    String preList(List<Todo> list, Repository<Integer, Todo> repository);

    String preTodo(Todo todo);
}
