package net.avdw.todo.list.addition;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import net.avdw.todo.repository.model.ATask;

public class AddEventDispatcher implements AListAddition {
    private EventBus eventBus;

    @Inject
    AddEventDispatcher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public ATask add(String summary) {
        try {
            throw new UnsupportedOperationException();
        } finally {
            eventBus.post(new AddEvent(summary));
        }
    }
}
