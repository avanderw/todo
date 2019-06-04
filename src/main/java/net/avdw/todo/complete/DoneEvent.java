package net.avdw.todo.complete;

import java.util.List;

class DoneEvent {
    List<Integer> idxs;

    DoneEvent(List<Integer> idxs) {
        this.idxs = idxs;
    }
}
