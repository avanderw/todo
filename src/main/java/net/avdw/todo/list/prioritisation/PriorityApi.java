package net.avdw.todo.list.prioritisation;

public interface PriorityApi {
    void add(Integer idx, PriorityInput priority);

    void remove(Integer idx);
}
