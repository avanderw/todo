package net.avdw.todo.repository;

import java.util.List;

public interface Repository<T> {
    void setAutoCommit(boolean autoCommit);

    List<T> findAll(Specification<T> specification);

    void add(T item);

    T findById(int id);

    void save(int id, T item);

    void commit();

    void addAll(List<T> itemList);

    void removeAll(Specification<T> specification);

    List<T> findAll();
}
