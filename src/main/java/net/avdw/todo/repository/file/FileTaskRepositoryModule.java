package net.avdw.todo.repository.file;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.model.ATask;

import java.nio.file.Path;

public class FileTaskRepositoryModule extends AbstractModule {
    private Path path;

    public FileTaskRepositoryModule(Path path) {
        this.path = path;
    }

    @Override
    protected void configure() {
        bind(new TypeLiteral<ARepository<ATask>>() {
        }).annotatedWith(FileTask.class).to(FileTaskRepository.class).in(Singleton.class);

        bind(FileTaskRepositoryEventListener.class).asEagerSingleton();
    }

    @Provides
    @FileTask
    Path fileTaskPath() {
        return path;
    }
}
