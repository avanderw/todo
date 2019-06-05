package net.avdw.todo.list.removal;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import java.util.*;

public class RemoveEventDispatcher implements RemoveApi{
    private EventBus eventBus;

    @Inject
    RemoveEventDispatcher(EventBus eventBus) {

        this.eventBus = eventBus;
    }

    public void remove(Integer idx) {
        eventBus.post(new RemoveEvent(Collections.singletonList(idx)));
    }

    public void remove(List<Integer> idxs) {
        eventBus.post(new RemoveEvent(idxs));
    }
}
