package net.avdw.todo.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.avdw.todo.Execution;
import net.avdw.todo.Global;
import net.avdw.todo.Local;
import org.pmw.tinylog.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TodoModule extends AbstractModule {
    private static final String DATA_DIRECTORY = ".todo";

    @Override
    protected void configure() {
        Path globalPath = Paths.get(System.getProperty("user.home")).resolve(DATA_DIRECTORY);
        bind(Path.class).annotatedWith(Execution.class).toInstance(Paths.get(""));
        bind(Path.class).annotatedWith(Global.class).toInstance(globalPath);

        install(new PropertyModule());
    }

    @Provides
    @Local
    private Path resolveLocalPath(final @Execution Path currentPath) {
        Logger.debug(String.format("Resolving: %s", currentPath.toAbsolutePath()));
        Path localRepositoryPath = currentPath.resolve(DATA_DIRECTORY);
        if (Files.exists(localRepositoryPath)) {
            return localRepositoryPath;
        } else if (currentPath.getParent() != null) {
            return resolveLocalPath(currentPath.getParent());
        } else {
            return Paths.get("").resolve(DATA_DIRECTORY);
        }
    }
}
