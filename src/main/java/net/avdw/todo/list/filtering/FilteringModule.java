package net.avdw.todo.list.filtering;

import com.google.inject.AbstractModule;

public class FilteringModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AFilter.class).annotatedWith(Context.class).to(ContextFilter.class);
        bind(AFilter.class).annotatedWith(Project.class).to(ProjectFilter.class);
        bind(AFilter.class).annotatedWith(TodoList.class).to(TodoListFilter.class);
    }
}
