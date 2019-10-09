package net.avdw.todo;

import com.google.inject.*;
import net.avdw.todo.config.LoggingSetup;
import net.avdw.todo.config.ProfilingModule;
import net.avdw.todo.config.TracingModule;
import net.avdw.todo.item.TodoItemModule;
import net.avdw.todo.property.PropertyModule;
import org.pmw.tinylog.Logger;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public final class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        CommandLine commandLine = new CommandLine(Todo.class, new GuiceFactory());
        commandLine.setCaseInsensitiveEnumValuesAllowed(true);
        commandLine.execute(args);
    }

    public static final class GuiceFactory implements CommandLine.IFactory {
        private static final String DATA_DIRECTORY = ".todo";
        private final Injector injector = Guice.createInjector(new GuiceModule());

        @Override
        public <K> K create(final Class<K> aClass) {
            return injector.getInstance(aClass);
        }

        static class GuiceModule extends AbstractModule {
            @Override
            protected void configure() {
                bind(List.class).to(LinkedList.class);

                install(new PropertyModule());
                bind(LoggingSetup.class).asEagerSingleton();

                install(new TracingModule());
                install(new ProfilingModule());

                Path globalPath = Paths.get(System.getProperty("user.home")).resolve(DATA_DIRECTORY);
                bind(Path.class).annotatedWith(Execution.class).toInstance(Paths.get("."));
                bind(Path.class).annotatedWith(GlobalTodo.class).toInstance(globalPath);
                bind(SimpleDateFormat.class).toInstance(new SimpleDateFormat("yyyy-MM-dd"));

                install(new TodoItemModule());
            }

            @Provides
            @Singleton
            @LocalTodo
            private Path resolveLocalPath(final @Execution Path currentPath) {
                Logger.debug(String.format("Resolving: %s", currentPath.toAbsolutePath()));
                Path localRepositoryPath = currentPath.resolve(DATA_DIRECTORY);
                if (Files.exists(localRepositoryPath)) {
                    Logger.debug(String.format("Path found: %s", localRepositoryPath));
                    return localRepositoryPath;
                } else if (currentPath.getParent() != null) {
                    Logger.debug("Path not found... continue through hierarchy");
                    return resolveLocalPath(currentPath.getParent());
                } else {
                    Logger.debug("Path not found... resolving to execution path");
                    return Paths.get(".").resolve(DATA_DIRECTORY);
                }
            }
        }
    }
}
