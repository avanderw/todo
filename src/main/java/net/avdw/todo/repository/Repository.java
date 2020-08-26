package net.avdw.todo.repository;

import java.util.Collection;

public interface Repository<T> {
    Collection<T> findAll(Specification<T> specification);

    void add(T item);
}
