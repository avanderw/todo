package net.avdw.todo.repository;

public interface FileTypeBuilder<T extends IdType> {
    public T build(final int idx, final String line);
}
