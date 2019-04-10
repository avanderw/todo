package net.avdw.todo.remove;

import com.google.inject.AbstractModule;

public class RemoveModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(RemoveApi.class).to(RemoveEventDispatcher.class);
        bind(RemoveTodoTxt.class).asEagerSingleton();
    }
}
