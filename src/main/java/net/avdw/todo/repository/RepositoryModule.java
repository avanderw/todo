package net.avdw.todo.repository;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import net.avdw.todo.repository.file.FileTaskRepositoryModule;
import net.avdw.todo.repository.memory.MemoryTask;
import net.avdw.todo.repository.memory.MemoryTaskRepository;
import net.avdw.todo.repository.memory.MemoryTaskRepositoryModule;
import net.avdw.todo.repository.model.ATask;
import net.avdw.todo.repository.file.FileTask;
import net.avdw.todo.repository.file.FileTaskRepository;

import java.nio.file.Path;

public class RepositoryModule extends AbstractModule {
    private Path path;

    public RepositoryModule(Path path) {
        this.path = path;
    }

    @Override
    protected void configure() {
        install(new MemoryTaskRepositoryModule());
        install(new FileTaskRepositoryModule(path));
    }
}
