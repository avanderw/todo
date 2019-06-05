package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import net.avdw.todo.add.AddApi;
import net.avdw.todo.add.AddTodoTxt;
import net.avdw.todo.complete.DoneApi;
import net.avdw.todo.complete.DoneTodoTxt;
import net.avdw.todo.list.ListApi;
import net.avdw.todo.list.ListTodo;
import net.avdw.todo.priority.PriorityApi;
import net.avdw.todo.priority.PriorityTodoTxt;
import net.avdw.todo.remove.RemoveApi;
import net.avdw.todo.remove.RemoveTodoTxt;
import net.avdw.todo.replace.ReplaceApi;
import net.avdw.todo.replace.ReplaceTodoTxt;

import java.io.File;
import java.text.SimpleDateFormat;

public class CucumberCtx {
    public <E> E getInstance(Class<E> addApiClass) {
        return getInjector().getInstance(addApiClass);
    }

    Injector injector;
    private Injector getInjector() {
        if (injector == null) {
            injector = Guice.createInjector(new Config());
        }
        return injector;
    }

    private class Config extends AbstractModule {
        String name;

        @Override
        protected void configure() {
            bind(AddApi.class).to(AddTodoTxt.class);
            bind(ReplaceApi.class).to(ReplaceTodoTxt.class);
            bind(ListApi.class).to(ListTodo.class);
            bind(DoneApi.class).to(DoneTodoTxt.class);
            bind(RemoveApi.class).to(RemoveTodoTxt.class);
            bind(PriorityApi.class).to(PriorityTodoTxt.class);
            bind(SimpleDateFormat.class).toInstance(new SimpleDateFormat("yyyy-MM-dd"));
        }

        @Provides
        File trackedFile() {
            return new File(name);
        }
    }
}
