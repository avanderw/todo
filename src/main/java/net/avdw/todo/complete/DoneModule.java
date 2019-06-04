package net.avdw.todo.complete;

import com.google.inject.AbstractModule;

public class DoneModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DoneApi.class).to(DoneEventDispatcher.class);
        bind(DoneTodoTxt.class).asEagerSingleton();
    }
}
