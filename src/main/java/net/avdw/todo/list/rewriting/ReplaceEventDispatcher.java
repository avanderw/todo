package net.avdw.todo.list.rewriting;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

public class ReplaceEventDispatcher implements ReplaceApi {
    private EventBus eventBus;

    @Inject
    ReplaceEventDispatcher(EventBus eventBus) {
        this.eventBus = eventBus;
    }
    @Override
    public void replace(Integer idx, String todoItem) {
        eventBus.post(new ReplaceEvent(idx, todoItem));
    }
}
