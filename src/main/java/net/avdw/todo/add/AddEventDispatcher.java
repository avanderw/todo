package net.avdw.todo.add;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import org.pmw.tinylog.Logger;

public class AddEventDispatcher implements AddApi {
    private EventBus eventBus;

    @Inject
    AddEventDispatcher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void add(String todo) {
        Logger.trace(todo); // research aspects
        eventBus.post(new AddEvent(todo));
    }
}
