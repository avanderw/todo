package net.avdw.todo.list.prioritisation;

import java.util.List;

class PriorityEvent {
    final List<Integer> idxs;
    final PriorityInput priority;

    PriorityEvent(List<Integer> idxs, PriorityInput priority) {
        this.idxs = idxs;
        this.priority = priority;
    }

    PriorityEvent(List<Integer> idxs) {
        this.idxs = idxs;
        this.priority = null;
    }
}
