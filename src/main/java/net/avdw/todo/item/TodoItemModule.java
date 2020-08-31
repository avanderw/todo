package net.avdw.todo.item;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

@Deprecated
public class TodoItemModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(TodoItem.class, TodoItem.class)
                .build(TodoItemFactory.class));
    }
}
