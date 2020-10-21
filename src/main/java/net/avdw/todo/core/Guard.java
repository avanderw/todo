package net.avdw.todo.core;

public interface Guard<T> {
    boolean isSatisfiedBy(T type);
}
