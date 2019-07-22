package net.avdw.todo.repository.finder;

import com.google.inject.AbstractModule;
import net.avdw.todo.repository.Global;
import net.avdw.todo.repository.Local;

public class RepositoryFinderModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ARepositoryFinder.class).annotatedWith(Local.class).to(LocalRepositoryFinder.class);
        bind(ARepositoryFinder.class).annotatedWith(Global.class).to(GlobalRepositoryFinder.class);
    }
}
