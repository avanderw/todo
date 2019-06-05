package net.avdw.todo.list.completion;

import java.util.List;

class DoneEvent {
    List<Integer> idxs;

    DoneEvent(List<Integer> idxs) {
        this.idxs = idxs;
    }
}
