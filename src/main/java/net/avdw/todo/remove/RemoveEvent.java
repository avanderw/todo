package net.avdw.todo.remove;

import java.util.List;

public class RemoveEvent {
    List<Integer> idxs;

    public RemoveEvent(List<Integer> idxs) {
        this.idxs = idxs;
    }
}
