package net.avdw.todo.repository;

public interface TypeBuilder<T> {
    public T build(final String line);
}
