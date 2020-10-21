package net.avdw.todo.core;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;

import java.util.List;

public interface Addon {
    String postRender(List<Todo> list, final Repository<Integer, Todo> repository);

    String preRender(List<Todo> list, final Repository<Integer, Todo> repository);
}
