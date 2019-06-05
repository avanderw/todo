package net.avdw.todo;

import com.google.inject.*;
import net.avdw.todo.add.AddApi;
import net.avdw.todo.add.AddTodoTxt;
import net.avdw.todo.complete.DoneApi;
import net.avdw.todo.complete.DoneTodoTxt;
import net.avdw.todo.list.ListApi;
import net.avdw.todo.list.ListTodo;
import net.avdw.todo.priority.PriorityApi;
import net.avdw.todo.priority.PriorityTodoTxt;
import net.avdw.todo.property.AProperty;
import net.avdw.todo.property.PropertyModule;
import net.avdw.todo.remove.RemoveApi;
import net.avdw.todo.remove.RemoveTodoTxt;
import net.avdw.todo.replace.ReplaceApi;
import net.avdw.todo.replace.ReplaceTodoTxt;
import net.avdw.todo.tracking.TrackApi;
import net.avdw.todo.tracking.TrackImpl;
import net.avdw.todo.tracking.TrackModule;
import net.avdw.todo.tracking.TrackedList;

import java.io.File;
import java.text.SimpleDateFormat;

public class CucumberCtx {
    public <E> E getInstance(Class<E> addApiClass) {
        return getInjector().getInstance(addApiClass);
    }

    public <T> T getInstance(Key<T> key) {
        return getInjector().getInstance(key);
    }


    Injector injector;
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
            install(new TrackModule());

            bind(AddApi.class).to(AddTodoTxt.class);
            bind(ReplaceApi.class).to(ReplaceTodoTxt.class);
            bind(ListApi.class).to(ListTodo.class);
            bind(DoneApi.class).to(DoneTodoTxt.class);
            bind(RemoveApi.class).to(RemoveTodoTxt.class);
            bind(PriorityApi.class).to(PriorityTodoTxt.class);
            bind(SimpleDateFormat.class).toInstance(new SimpleDateFormat("yyyy-MM-dd"));
        }

    }
}
