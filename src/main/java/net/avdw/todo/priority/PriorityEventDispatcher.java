package net.avdw.todo.priority;

import com.google.common.eventbus.EventBus;

import java.util.Collections;

public class PriorityEventDispatcher implements PriorityApi {
    private EventBus eventBus;

    PriorityEventDispatcher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void add(Integer idx, PriorityInput priority) {
        eventBus.post(new PriorityEvent(Collections.singletonList(idx), priority));
    }

    @Override
    public void remove(Integer idx) {
        eventBus.post(new PriorityEvent(Collections.singletonList(idx)));
    }
}