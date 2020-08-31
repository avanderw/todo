package net.avdw.todo.repository;

import java.util.List;

public interface Repository<I, T extends IdType<I>> {
    void setAutoCommit(boolean autoCommit);

    List<T> findAll(Specification<I, T> specification);

    void add(T item);

    T findById(int id);

    void update(T item);

    void commit();

    void addAll(List<T> itemList);

    void removeAll(Specification<I, T> specification);

    List<T> findAll();

    int size();
}
