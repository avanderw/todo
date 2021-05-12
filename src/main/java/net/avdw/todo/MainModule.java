package net.avdw.todo;

import dagger.Module;
import dagger.Provides;
import net.avdw.todo.core.style.StyleModule;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;

import javax.inject.Singleton;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

@Module(includes = StyleModule.class)
abstract class MainModule {
    @Provides
    @Singleton
    static Path todoPath() {
        final Path todoPath = Paths.get(".todo/todo.txt");
        if (Files.exists(todoPath)) {
            return todoPath;
        } else {
            return Paths.get(System.getProperty("user.home")).resolve(".todo/todo.txt");
        }
    }

    @Provides
    @Singleton
    static Repository<Integer, Todo> todoRepository(final Path todoPath) {
        return new FileRepository<>(todoPath, new TodoFileTypeBuilder());
    }

    @Provides
    @Singleton
    static ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("messages", Locale.ENGLISH);
    }
}
