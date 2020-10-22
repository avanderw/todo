package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import net.avdw.todo.core.Addon;
import net.avdw.todo.plugin.change.ChangeAddon;
import net.avdw.todo.plugin.progress.ProgressAddon;
import net.avdw.todo.plugin.timing.CycleTimeAddon;
import net.avdw.todo.plugin.timing.LeadTimeAddon;
import net.avdw.todo.plugin.timing.ReactionTimeAddon;

public class AddonModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<Addon> addons = Multibinder.newSetBinder(binder(), Addon.class);
        addons.addBinding().to(ProgressAddon.class);
        addons.addBinding().to(ChangeAddon.class);
        addons.addBinding().to(ReactionTimeAddon.class);
        addons.addBinding().to(CycleTimeAddon.class);
        addons.addBinding().to(LeadTimeAddon.class);
    }
}
