package net.avdw.todo.add;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scope;
import com.google.inject.Singleton;

public class AddModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AddApi.class).to(AddEventDispatcher.class);
        bind(AddTodoTxt.class).asEagerSingleton();
        bind(AddWunderlist.class).asEagerSingleton();
    }
}
