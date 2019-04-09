package net.avdw.todo.remove;

import java.util.List;

public interface RemoveApi {
    void remove(Integer idx);

    void remove(List<Integer> args);
}
