package net.avdw.todo.priority;

import com.google.inject.AbstractModule;

public class PriorityModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PriorityApi.class).to(PriorityEventDispatcher.class);
        bind(PriorityTodoTxt.class).asEagerSingleton();
    }
}
