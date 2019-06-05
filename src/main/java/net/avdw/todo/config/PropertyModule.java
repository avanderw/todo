package net.avdw.todo.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import net.avdw.todo.list.tracking.TrackedList;

public class PropertyModule extends AbstractModule {
    @Override
    protected void configure() {
        APropertyRepository repository = new PropertyFile("todo");
        Names.bindProperties(binder(), repository.getProperties());
        bind(APropertyRepository.class).toInstance(repository);
    }

    @TrackedList
    @Provides
    AProperty property(APropertyRepository repository) {
        return new AProperty(repository, TrackedList.name);
    }
}
