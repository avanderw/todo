package net.avdw.todo.domain;

import net.avdw.todo.repository.FileTypeBuilder;

public class TodoFileTypeBuilder implements FileTypeBuilder<Todo> {
    @Override
    public Todo build(final int idx, final String line) {
        return new Todo(idx, line);
    }
}
