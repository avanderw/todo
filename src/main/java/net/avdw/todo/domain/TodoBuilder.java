package net.avdw.todo.domain;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.TypeBuilder;

public class TodoBuilder implements TypeBuilder<Todo> {
    @Override
    public Todo build(final String line) {
        return new Todo(line);
    }
}
