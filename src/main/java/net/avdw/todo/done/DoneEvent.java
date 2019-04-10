package net.avdw.todo.done;

import java.util.List;

class DoneEvent {
    List<Integer> idxs;

    DoneEvent(List<Integer> idxs) {
        this.idxs = idxs;
    }
}
