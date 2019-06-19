package net.avdw.todo.admin.initialize;

import com.google.inject.AbstractModule;

public class InitializeModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AInitializer.class).to(FileInitializer.class);
    }
}
