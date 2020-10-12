package net.avdw.todo;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import picocli.CommandLine;

public final class TestGuiceFactory implements CommandLine.IFactory {
    private final Module module;
    private Injector injector;

    public TestGuiceFactory(final Module module) {
        this.module = module;

        reset();
    }

    public void reset() {
        injector = Guice.createInjector(module);
    }

    @Override
    public <K> K create(final Class<K> aClass) {
        return injector.getInstance(aClass);
    }

}
