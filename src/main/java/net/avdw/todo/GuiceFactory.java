package net.avdw.todo;

import com.google.inject.*;
import picocli.CommandLine;

public final class GuiceFactory implements CommandLine.IFactory {
    private final Injector injector = Guice.createInjector(new GuiceModule());

    @Override
    public <K> K create(final Class<K> aClass) {
        return injector.getInstance(aClass);
    }

}
