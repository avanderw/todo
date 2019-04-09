package net.avdw.todo.add;

import com.google.inject.AbstractModule;

public class AddModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AddApi.class).to(AddEventDispatcher.class);
        bind(AddTodoTxt.class).asEagerSingleton();
        bind(AddWunderlist.class).asEagerSingleton();
    }
}
