package net.avdw.todo;

import com.google.common.eventbus.EventBus;
import com.google.inject.*;
import net.avdw.todo.add.AddModule;
import net.avdw.todo.add.AddTodoTxt;
import net.avdw.todo.add.AddWunderlist;
import picocli.CommandLine;

import java.util.Set;

public class GuiceFactory implements CommandLine.IFactory {
    private final Injector injector = Guice.createInjector(new TodoModule());

    @Override
    public <K> K create(Class<K> aClass) throws Exception {
        return injector.getInstance(aClass);
    }

    static class TodoModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new AddModule());
        }

        @Provides
        @Singleton
        EventBus eventBus() {
            EventBus eventBus = new EventBus();
            eventBus.register(new AddTodoTxt());
            eventBus.register(new AddWunderlist()); // research guice with guava eventbus
            return eventBus;
        }
    }
}
