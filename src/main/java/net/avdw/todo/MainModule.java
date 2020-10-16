package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.ext.StartedExt;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.style.StyleModule;
import picocli.CommandLine;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
        bind(ResourceBundle.class).toInstance(ResourceBundle.getBundle("messages", Locale.ENGLISH));
        bind(StartedExt.class).toInstance(new StartedExt("started"));
        bind(PrintWriter.class).annotatedWith(Names.named("out")).toInstance(new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true));
        bind(PrintWriter.class).annotatedWith(Names.named("err")).toInstance(new PrintWriter(new OutputStreamWriter(System.err, StandardCharsets.UTF_8), true));

        install(new StyleModule());
    }

    @Provides
    @Singleton
    Path todoPath() {
        Path todoPath = Paths.get(".todo/todo.txt");
        if (Files.exists(todoPath)) {
            return todoPath;
        } else {
            return Paths.get(System.getProperty("user.home")).resolve(".todo/todo.txt");
        }
    }

    @Provides
    @Singleton
    Repository<Integer, Todo> todoRepository(final Path todoPath) {
        return new FileRepository<>(todoPath, new TodoFileTypeBuilder());
    }
}
