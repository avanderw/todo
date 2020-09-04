package net.avdw.todo.repository;

public interface IdType<T> {
    T getId();
    void setId(T id);
}
