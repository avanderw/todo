package net.avdw.todo;

import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import picocli.CommandLine;

public class Main {
    public static final EventBus EVENT_BUS = new EventBus();

    public static void main(String[] args) {
        Logger.getConfiguration()
                .formatPattern("{date:yyyy-MM-dd HH:mm:ss} [{thread}] {class}.{method}() {level}: {message}")
                .level(Level.TRACE).activate();
        CommandLine.run(TodoCli.class, new GuiceFactory(), args);
    }

    public static class GuiceFactory implements CommandLine.IFactory {
        private final Injector injector = Guice.createInjector(new TodoModule());


        @Override
        public <K> K create(Class<K> aClass) {
            return injector.getInstance(aClass);
        }
    }

}
