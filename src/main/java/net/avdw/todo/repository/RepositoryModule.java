package net.avdw.todo.repository;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import net.avdw.todo.repository.file.FileTaskRepositoryModule;
import net.avdw.todo.repository.memory.MemoryTask;
import net.avdw.todo.repository.memory.MemoryTaskRepository;
import net.avdw.todo.repository.model.ATask;
import net.avdw.todo.repository.file.FileTask;
import net.avdw.todo.repository.file.FileTaskRepository;

public class RepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<ARepository<ATask>>(){}).annotatedWith(MemoryTask.class).to(MemoryTaskRepository.class).in(Singleton.class);
        install(new FileTaskRepositoryModule());
    }
}
