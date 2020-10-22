package net.avdw.todo;

public interface Guard<T> {
    boolean isSatisfiedBy(T type);
}
