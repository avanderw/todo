package net.avdw.todo.list.removal;

import java.util.List;

public interface RemoveApi {
    void remove(Integer idx);

    void remove(List<Integer> args);
}
