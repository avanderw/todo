package net.avdw.todo.complete;

import java.util.List;

public interface DoneApi {
    void done(Integer idx);

    void done(List<Integer> args);
}
