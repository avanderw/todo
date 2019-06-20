package net.avdw.todo.admin.initialize;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.file.FileTask;
import net.avdw.todo.repository.file.FileTaskRepository;
import net.avdw.todo.repository.file.FileTaskRepositoryModule;
import net.avdw.todo.repository.model.ATask;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileInitializer implements AInitializer {

    @Override
    public void init() {
        initialize(Paths.get("."));
    }

    @Override
    public void init(Path path) {
        initialize(path);
    }

    private void initialize(Path path) {
        Guice.createInjector(new FileTaskRepositoryModule(path))
                .getInstance(Key.get(new TypeLiteral<ARepository<ATask>>(){}, FileTask.class))
                .init();
    }
}
