package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.style.StyleModule;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

class MainModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(RunningStats.class).toInstance(new RunningStats());
        bind(List.class).to(LinkedList.class);
        bind(Set.class).to(HashSet.class);
        bind(Path.class).toInstance(Paths.get(".todo/todo.txt"));
        bind(ResourceBundle.class).toInstance(ResourceBundle.getBundle("messages", Locale.getDefault()));

        install(new StyleModule());
    }

    @Provides
    @Singleton
    Repository<Integer, Todo> todoRepository(final Path todoPath) {
        return new FileRepository<>(todoPath, new TodoFileTypeBuilder());
    }
}
