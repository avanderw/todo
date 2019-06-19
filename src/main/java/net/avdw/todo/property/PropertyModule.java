package net.avdw.todo.property;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import net.avdw.todo.list.tracking.TrackedList;

import java.nio.file.Path;

public class PropertyModule extends AbstractModule {
    private Path propertyDir;

    public PropertyModule(Path propertyDir) {
        this.propertyDir = propertyDir;
    }

    @Override
    protected void configure() {
        APropertyRepository repository = new PropertyFile(propertyDir);
        Names.bindProperties(binder(), repository.getProperties());
        bind(APropertyRepository.class).toInstance(repository);
    }

    @TrackedList
    @Provides
    AProperty property(APropertyRepository repository) {
        return new AProperty(repository, TrackedList.name);
    }
}
