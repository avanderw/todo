package net.avdw.todo.core.selector;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import net.avdw.todo.extension.SelectorLoader;

public class SelectorModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<Selector> selectorSet = Multibinder.newSetBinder(binder(), Selector.class);
        selectorSet.addBinding().to(ContextSelector.class);
        selectorSet.addBinding().to(ProjectSelector.class);
        SelectorLoader pluginLoader = new SelectorLoader();
        pluginLoader.getSelectorSet().forEach(selector -> selectorSet.addBinding().to(selector.getClass()));
    }
}
