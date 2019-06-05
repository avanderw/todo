package net.avdw.todo;

import com.google.inject.*;
import net.avdw.todo.config.PropertyModule;
import net.avdw.todo.list.tracking.TrackingModule;

import java.text.SimpleDateFormat;

public class CucumberCtx {
    public <E> E getInstance(Class<E> addApiClass) {
        return getInjector().getInstance(addApiClass);
    }

    public <T> T getInstance(Key<T> key) {
        return getInjector().getInstance(key);
    }


    private Injector injector;
    private Injector getInjector() {
        if (injector == null) {
            injector = Guice.createInjector(new Config());
        }
        return injector;
    }


    private class Config extends AbstractModule {

        @Override
        protected void configure() {
            install(new PropertyModule());
            install(new TrackingModule());

//            bind(AListAddition.class).to(AddTodoTxt.class);
//            bind(ReplaceApi.class).to(ReplaceTodoTxt.class);
//            bind(ListApi.class).to(ListTodo.class);
//            bind(DoneApi.class).to(DoneTodoTxt.class);
//            bind(RemoveApi.class).to(RemoveTodoTxt.class);
//            bind(PriorityApi.class).to(PriorityTodoTxt.class);
            bind(SimpleDateFormat.class).toInstance(new SimpleDateFormat("yyyy-MM-dd"));
        }

    }
}
