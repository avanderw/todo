package net.avdw.todo.repository.file;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.model.ATask;

import java.nio.file.Path;

public class FileTaskRepositoryModule extends AbstractModule {
    public FileTaskRepositoryModule(Path path) {

    }

    @Override
    protected void configure() {
        bind(new TypeLiteral<ARepository<ATask>>(){}).annotatedWith(FileTask.class).to(FileTaskRepository.class).in(Singleton.class);
    }
}
