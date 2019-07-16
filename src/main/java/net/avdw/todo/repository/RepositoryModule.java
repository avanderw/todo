package net.avdw.todo.repository;

import com.google.inject.AbstractModule;

public class RepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ARepository.class).to(FileRepository.class);
        bind(String.class).annotatedWith(RepositoryPath.class).toInstance(".todo");
    }
}
