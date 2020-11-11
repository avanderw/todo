package net.avdw.todo.core.selector;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import net.avdw.todo.plugin.PluginLoader;

public class SelectorModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<Selector> selectorSet = Multibinder.newSetBinder(binder(), Selector.class);
        selectorSet.addBinding().to(ContextSelector.class);
        selectorSet.addBinding().to(ProjectSelector.class);
        PluginLoader pluginLoader = new PluginLoader();
        pluginLoader.getSelectorSet().forEach(selector -> selectorSet.addBinding().to(selector.getClass()));
    }
}
