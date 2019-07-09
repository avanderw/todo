package net.avdw.todo;

import com.google.inject.AbstractModule;
import net.avdw.todo.admin.initialize.InitializeModule;
import net.avdw.todo.eventbus.EventBusModule;
import net.avdw.todo.list.addition.AdditionModule;
import net.avdw.todo.list.filtering.FilteringModule;
import net.avdw.todo.property.PropertyModule;
import net.avdw.todo.repository.RepositoryModule;

import java.nio.file.Paths;

public class TodoModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LoggingModule());
        install(new PropertyModule(Paths.get(System.getProperty("user.home"))));
        install(new EventBusModule("Main"));
        install(new RepositoryModule(Paths.get(".")));

        install(new InitializeModule());
        install(new FilteringModule());
        install(new AdditionModule());
    }
}