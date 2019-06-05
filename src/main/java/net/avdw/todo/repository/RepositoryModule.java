package net.avdw.todo.repository;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import net.avdw.todo.repository.memory.Memory;
import net.avdw.todo.repository.memory.MemoryTaskRepository;
import net.avdw.todo.repository.model.ATask;
import net.avdw.todo.repository.plaintext.PlainText;
import net.avdw.todo.repository.plaintext.PlainTextTaskRepository;

public class RepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<ARepository<ATask>>(){}).annotatedWith(Memory.class).to(MemoryTaskRepository.class).in(Singleton.class);
        bind(new TypeLiteral<ARepository<ATask>>(){}).annotatedWith(PlainText.class).to(PlainTextTaskRepository.class).in(Singleton.class);
    }
}
