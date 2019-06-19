package net.avdw.todo.repository.memory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.model.ATask;

public class MemoryTaskRepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<ARepository<ATask>>() {
        }).annotatedWith(MemoryTask.class).to(MemoryTaskRepository.class).in(Singleton.class);
    }
}
