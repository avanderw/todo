package net.avdw.todo;

import dagger.Module;
import dagger.Provides;
import net.avdw.todo.core.style.StyleModule;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;

import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;

@Module(includes = StyleModule.class)
public
class TestMainModule {

    private final Path todoPath;

    public TestMainModule(Path todoPath) {
        this.todoPath = todoPath;
    }

    @Provides
    @Singleton
    static Repository<Integer, Todo> todoRepository(Path todoPath) {
        return new FileRepository<>(todoPath, new TodoFileTypeBuilder());
    }

    @Provides
    @Singleton
    static ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("messages", Locale.ENGLISH);
    }

    @Provides
    @Singleton
    Path todoPath() {
        return todoPath;
    }
}
