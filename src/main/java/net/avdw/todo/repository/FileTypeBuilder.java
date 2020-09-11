package net.avdw.todo.repository;

public interface FileTypeBuilder<T extends IdType> {
    T build(int idx, String line);
}
