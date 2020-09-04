package net.avdw.todo.repository;

import java.util.List;

public interface Repository<I, T extends IdType<I>> {
    void add(T item);

    void addAll(List<T> itemList);

    void commit();

    List<T> findAll(Specification<I, T> specification);

    T findById(int id);

    void removeAll(Specification<I, T> specification);

    void setAutoCommit(boolean autoCommit);

    int size();

    void update(T item);
}
