package net.avdw.todo.repository;

import net.avdw.todo.repository.model.AItem;
import net.avdw.todo.repository.model.ATask;

import java.util.List;
import java.util.function.Predicate;

public interface ARepository<T extends AItem> {
    void add(ATask task);
    T retrieve(int id);
    T update(T t);
    T delete(T t);

    List<T> list();
    List<T> list(Predicate<T> predicate);
    void saveList(List<T> list);
}
