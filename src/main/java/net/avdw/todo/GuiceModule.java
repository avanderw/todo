package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.avdw.todo.config.LoggingSetup;
import net.avdw.todo.config.ProfilingModule;
import net.avdw.todo.file.TodoFileModule;
import net.avdw.todo.item.TodoItemModule;
import net.avdw.todo.number.NumberModule;
import net.avdw.todo.property.PropertyModule;
import net.avdw.todo.theme.ThemeModule;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

class GuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(RunningStats.class).toInstance(new RunningStats());
        bind(List.class).to(LinkedList.class);

        install(new PropertyModule());
        install(new NumberModule());
        install(new ThemeModule());

        bind(LoggingSetup.class).asEagerSingleton();

        install(new ProfilingModule());

        Path globalPath = Paths.get(System.getProperty("user.home")).resolve(".todo");
        bind(Path.class).annotatedWith(Global.class).toInstance(globalPath);
        bind(Path.class).annotatedWith(Execution.class).toInstance(Paths.get(""));
        bind(SimpleDateFormat.class).toInstance(new SimpleDateFormat("yyyy-MM-dd"));

        install(new TodoItemModule());
        install(new TodoFileModule());
    }

    @Provides
    @Singleton
    @Working
    Path workingTodoFilePath(@Global final Path globalTodoDirectoryPath) {
        Path workingTodoFilePath;
        Path localTodoDirectoryPath = resolveLocalTodoDirectoryPath(Paths.get(""));
        if (Files.exists(localTodoDirectoryPath)) {
            workingTodoFilePath = localTodoDirectoryPath.resolve("todo.txt");
        } else {
            workingTodoFilePath = globalTodoDirectoryPath.resolve("todo.txt");
        }
        return workingTodoFilePath;
    }

    @Provides
    @Singleton
    @Removed
    Path removedTodoFilePath(@Working final Path workingTodoFilePath) {
        return workingTodoFilePath.getParent().resolve("removed.txt");
    }

    @Provides
    @Singleton
    @Parked
    Path parkedTodoFilePath(@Working final Path workingTodoFilePath) {
        return workingTodoFilePath.getParent().resolve("parked.txt");
    }

    @Provides
    @Singleton
    @Done
    Path doneTodoFilePath(@Working final Path workingTodoFilePath) {
        return workingTodoFilePath.getParent().resolve("done.txt");
    }

    Path resolveWorkingTodoFilePath(final Path globalFilePath) {
        Logger.debug("Resolving working todo path");
        Path workingPath;
        Path localPath = resolveLocalTodoFilePath(Paths.get("todo.txt"));

        if (Files.exists(localPath)) {
            Logger.debug("Found local todo path: {}", localPath);
            workingPath = localPath;
        } else {
            Logger.debug("Could not resolve local todo path");
            if (Files.exists(globalFilePath)) {
                Logger.debug("Found global todo path: {}", globalFilePath);
                workingPath = globalFilePath;
            } else {
                Logger.debug("Could not resolve global todo path");
                try {
                    Files.createDirectories(globalFilePath.getParent());
                    Files.createFile(globalFilePath);
                    Logger.debug("Created global todo file");
                } catch (IOException e) {
                    Logger.error(e.getMessage());
                    Logger.debug(e);
                }
                workingPath = globalFilePath;
            }
        }
        return workingPath;
    }

    private Path resolveLocalTodoFilePath(final Path currentFilePath) {
        Logger.debug("Resolving local todo file path [currentFilePath={}]", currentFilePath.toAbsolutePath());
        Path localFilePath;
        if (Files.exists(currentFilePath)) {
            localFilePath = currentFilePath;
        } else {
            Path currentDirectoryPath = currentFilePath.getParent();
            if (currentDirectoryPath == null) {
                localFilePath = Paths.get("todo.txt");
            } else {
                Path parentDirectoryPath = currentDirectoryPath.getParent();
                Path parentDirectoryFilePath = parentDirectoryPath.resolve("todo.txt");
                localFilePath = resolveLocalTodoFilePath(parentDirectoryFilePath);
            }
        }

        Logger.debug("Using local todo file path: {}", localFilePath);
        return localFilePath;
    }

    @Provides
    @Singleton
    @LocalTodo
    private Path resolveLocalTodoDirectoryPath(final @Execution Path currentPath) {
        Logger.debug(String.format("Resolving: %s", currentPath.toAbsolutePath()));
        Path localRepositoryPath = currentPath.resolve(".todo");
        if (Files.exists(localRepositoryPath)) {
            Logger.debug(String.format("Path found: %s", localRepositoryPath));
            return localRepositoryPath;
        } else if (currentPath.getParent() != null) {
            Logger.debug("Path not found... continue through hierarchy");
            return resolveLocalTodoDirectoryPath(currentPath.getParent());
        } else {
            Logger.debug("Path not found... resolving to execution path");
            return Paths.get(".").resolve(".todo");
        }
    }
}
