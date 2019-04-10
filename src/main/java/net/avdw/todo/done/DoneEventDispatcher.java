package net.avdw.todo.done;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import java.util.Collections;
import java.util.List;

public class DoneEventDispatcher implements DoneApi {
    private EventBus eventBus;

    @Inject
    DoneEventDispatcher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void done(Integer idx) {
        eventBus.post(new DoneEvent(Collections.singletonList(idx)));
    }

    @Override
    public void done(List<Integer> idxs) {
        eventBus.post(new DoneEvent(idxs));

    }
}
