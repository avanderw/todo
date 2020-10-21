package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import net.avdw.todo.core.Addon;
import net.avdw.todo.plugin.change.ChangeAddon;
import net.avdw.todo.plugin.progress.ProgressAddon;

public class AddonModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<Addon> addons = Multibinder.newSetBinder(binder(), Addon.class);
        addons.addBinding().to(ProgressAddon.class);
        addons.addBinding().to(ChangeAddon.class);
    }
}
