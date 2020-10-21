package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import net.avdw.todo.core.Addon;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.plugin.progress.ProgressExtension;
import net.avdw.todo.plugin.Plugin;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.style.StyleModule;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.tinylog.Logger;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
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
        bind(ProgressExtension.class).toInstance(new ProgressExtension("started"));
        bind(PrintWriter.class).annotatedWith(Names.named("out")).toInstance(new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true));
        bind(PrintWriter.class).annotatedWith(Names.named("err")).toInstance(new PrintWriter(new OutputStreamWriter(System.err, StandardCharsets.UTF_8), true));

        Reflections reflection = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("net.avdw.todo.plugin"))
                .setScanners(new SubTypesScanner()));
        reflection.getSubTypesOf(Module.class).stream().filter(m->m.isAnnotationPresent(Plugin.class)).forEach(module -> {
            try {
                Logger.debug("Registering plugin module: {}", module.getSimpleName());
                install(module.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                Logger.error(e);
            }
        });
        install(new StyleModule());

        Multibinder<Addon> addons = Multibinder.newSetBinder(binder(), Addon.class);
        reflection.getSubTypesOf(Addon.class).forEach(addon -> addons.addBinding().to(addon));
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
