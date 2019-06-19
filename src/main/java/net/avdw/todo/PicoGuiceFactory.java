package net.avdw.todo;

import com.google.inject.*;
import picocli.CommandLine;

public class PicoGuiceFactory implements CommandLine.IFactory {
    private final Injector injector = Guice.createInjector(new TodoModule());

    @Override
    public <K> K create(Class<K> aClass) throws Exception {
        return injector.getInstance(aClass);
    }


}
