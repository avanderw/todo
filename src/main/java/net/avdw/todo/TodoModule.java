package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.pmw.tinylog.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TodoModule extends AbstractModule {
    private static final String dataDirectory = ".todo";

    @Override
    protected void configure() {
        install(new LoggingModule());
        install(new ProfilingModule());

        bind(Path.class).annotatedWith(Execution.class).toInstance(Paths.get(""));
    }

    @Provides
    @Global
    private Path globalPath() {
        return Paths.get(System.getProperty("user.home")).resolve(dataDirectory);
    }

    @Provides
    @Local
    private Path resolveLocalPath(@Execution Path currentPath) {
        Logger.debug(String.format("Resolving: %s", currentPath.toAbsolutePath()));
        Path localRepositoryPath = currentPath.resolve(dataDirectory);
        if (Files.exists(localRepositoryPath)) {
            return localRepositoryPath;
        } else if (currentPath.getParent() != null) {
            return resolveLocalPath(currentPath.getParent());
        } else {
            return Paths.get("").resolve(dataDirectory);
        }
    }
}
