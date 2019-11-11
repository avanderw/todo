package net.avdw.todo.file;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class TodoFileModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(TodoFile.class, TodoFile.class)
                .build(TodoFileFactory.class));
    }
}
