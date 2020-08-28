package net.avdw.todo;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import picocli.CommandLine;

public final class GuiceFactory implements CommandLine.IFactory {
    private final Injector injector;

    GuiceFactory(Module module) {
        injector = Guice.createInjector(module);
    }

    @Override
    public <K> K create(final Class<K> aClass) {
        return injector.getInstance(aClass);
    }

}
