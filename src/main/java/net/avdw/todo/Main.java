package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.avdw.todo.config.LoggingModule;
import net.avdw.todo.config.ProfilingModule;
import net.avdw.todo.config.TodoModule;
import picocli.CommandLine;

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
        private final Injector injector = Guice.createInjector(new GuiceModule());

        @Override
        public <K> K create(final Class<K> aClass) {
            return injector.getInstance(aClass);
        }

        static class GuiceModule extends AbstractModule {
            @Override
            protected void configure() {
                install(new LoggingModule());
                install(new ProfilingModule());
                install(new TodoModule());

                bind(List.class).to(LinkedList.class);
            }
        }
    }
}
