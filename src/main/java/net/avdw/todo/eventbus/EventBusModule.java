package net.avdw.todo.eventbus;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.pmw.tinylog.Logger;

public class EventBusModule extends AbstractModule {
    private final String name;

    public EventBusModule(String name) {
        this.name = name;
    }

    @Override
    protected void configure() {
        EventBus eventBus = new EventBus(String.format("%s EventBus", name));
        bind(EventBus.class).toInstance(eventBus);
        bindListener(Matchers.any(), new TypeListener() {
            public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                typeEncounter.register((InjectionListener<I>) eventBus::register);
            }
        });
        eventBus.register(new DeadEventListener());
    }

    private class DeadEventListener {
        @Subscribe
        public void handleDeadEvent(DeadEvent deadEvent) {
            Logger.warn(deadEvent);
        }
    }
}
