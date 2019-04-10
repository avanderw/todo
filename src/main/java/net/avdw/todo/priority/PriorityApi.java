package net.avdw.todo.priority;

public interface PriorityApi {
    void add(Integer idx, PriorityInput priority);

    void remove(Integer idx);
}
