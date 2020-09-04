package net.avdw.todo.repository;

public interface FileTypeBuilder<T extends IdType> {
    T build(final int idx, final String line);
}
