package net.avdw.todo;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.avdw.todo.config.TodoModule;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        CommandLine.run(Todo.class, new GuiceFactory(), args);
    }

    public static class GuiceFactory implements CommandLine.IFactory {
        private final Injector injector = Guice.createInjector(new TodoModule());

        @Override
        public <K> K create(Class<K> aClass) {
            return injector.getInstance(aClass);
        }
    }
}
