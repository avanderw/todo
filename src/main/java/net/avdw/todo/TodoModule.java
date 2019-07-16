package net.avdw.todo;

import com.google.inject.AbstractModule;
import net.avdw.todo.repository.RepositoryModule;

public class TodoModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LoggingModule());
        install(new ProfilingModule());

        install(new RepositoryModule());
    }
}