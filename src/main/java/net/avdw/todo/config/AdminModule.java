package net.avdw.todo.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.avdw.todo.Todo;

public class AdminModule extends AbstractModule {
    @Provides
    private Boolean showAll(Todo todo) {
        return todo.showAll();
    }
}

