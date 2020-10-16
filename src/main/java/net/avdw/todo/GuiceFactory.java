package net.avdw.todo;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import picocli.CommandLine;

public final class GuiceFactory implements CommandLine.IFactory {
    private final Injector injector;

    GuiceFactory(final Injector injector) {
        this.injector = injector;
    }

    @Override
    public <K> K create(final Class<K> aClass) {
        return injector.getInstance(aClass);
    }

    public <K> K create(final Key<K> aKey) {
        return injector.getInstance(aKey);
    }

}
