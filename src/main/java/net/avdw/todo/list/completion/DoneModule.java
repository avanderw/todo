package net.avdw.todo.list.completion;

import com.google.inject.AbstractModule;

public class DoneModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DoneApi.class).to(DoneEventDispatcher.class);
        bind(DoneTodoTxt.class).asEagerSingleton();
    }
}
