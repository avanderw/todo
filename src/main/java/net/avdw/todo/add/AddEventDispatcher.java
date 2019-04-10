package net.avdw.todo.add;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

public class AddEventDispatcher implements AddApi {
    private EventBus eventBus;

    @Inject
    AddEventDispatcher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void add(String todo) {
        eventBus.post(new AddEvent(todo));
    }
}
