package net.avdw.todo.core.selector;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import net.avdw.todo.plugin.moscow.MoscowExtSelector;

public class SelectorModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<Selector> selectorSet = Multibinder.newSetBinder(binder(), Selector.class);
        selectorSet.addBinding().toInstance(new ExtSelector("importance:"));
        selectorSet.addBinding().toInstance(new ExtSelector("urgency:"));
        selectorSet.addBinding().toInstance(new ExtSelector("size:"));
        selectorSet.addBinding().to(MoscowExtSelector.class);
    }
}
