package net.avdw.todo;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import net.avdw.todo.add.AddModule;

public class TodoModule extends AbstractModule {
    @Override
    protected void configure() {
        EventBus eventBus = new EventBus("Todo EventBus");
        bind(EventBus.class).toInstance(eventBus);
        bindListener(Matchers.any(), new TypeListener() {
            public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                typeEncounter.register((InjectionListener<I>) eventBus::register);
            }
        });

        install(new AddModule());
    }
}