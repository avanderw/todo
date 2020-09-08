package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;

import java.nio.file.Path;
import java.util.*;

public class TestModule extends AbstractModule {
    private final Path todoPath;

    public TestModule(final Path todoPath) {
        this.todoPath = todoPath;
    }

    @Override
    protected void configure() {
        bind(List.class).to(LinkedList.class);
        bind(Set.class).to(HashSet.class);
        bind(Path.class).toInstance(todoPath);
        bind(ResourceBundle.class).toInstance(ResourceBundle.getBundle("messages", Locale.getDefault()));
    }

    @Provides
    @Singleton
    Repository<Integer, Todo> todoRepository(final Path todoPath) {
        return new FileRepository<>(todoPath, new TodoFileTypeBuilder());
    }
}
