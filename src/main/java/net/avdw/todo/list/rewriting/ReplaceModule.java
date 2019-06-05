package net.avdw.todo.list.rewriting;

import com.google.inject.AbstractModule;

public class ReplaceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ReplaceApi.class).to(ReplaceEventDispatcher.class);
        bind(ReplaceTodoTxt.class).asEagerSingleton();
    }
}
