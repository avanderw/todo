package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.plugin.Plugin;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.style.StyleModule;
import org.fusesource.jansi.AnsiConsole;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.tinylog.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

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
        bind(ByteArrayOutputStream.class).annotatedWith(Names.named("out")).toInstance(new ByteArrayOutputStream());
        bind(ByteArrayOutputStream.class).annotatedWith(Names.named("err")).toInstance(new ByteArrayOutputStream());

        Reflections reflection = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("net.avdw.todo.plugin"))
                .setScanners(new SubTypesScanner()));
        reflection.getSubTypesOf(Module.class).stream().filter(m->m.isAnnotationPresent(Plugin.class)).forEach(module -> {
            try {
                Logger.debug("Registering module: {}", module);
                install(module.getDeclaredConstructor().newInstance());

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                Logger.error(e);
            }
        });
        install(new StyleModule());
    }

    @Provides
    @Singleton
    @Named("err")
    PrintWriter err(@Named("err") final ByteArrayOutputStream err) {
        return new PrintWriter(AnsiConsole.wrapOutputStream(err), true, StandardCharsets.UTF_8);
    }

    @Provides
    @Singleton
    @Named("out")
    PrintWriter out(@Named("out") final ByteArrayOutputStream out) {
        return new PrintWriter(AnsiConsole.wrapOutputStream(out), true, StandardCharsets.UTF_8);
    }

    @Provides
    @Singleton
    Repository<Integer, Todo> todoRepository(final Path todoPath) {
        return new FileRepository<>(todoPath, new TodoFileTypeBuilder());
    }
}
