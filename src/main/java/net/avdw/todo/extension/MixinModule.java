package net.avdw.todo.extension;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import net.avdw.todo.extension.change.ChangeAddon;
import net.avdw.todo.extension.progress.ProgressAddon;
import net.avdw.todo.extension.state.StateAddon;
import net.avdw.todo.extension.timing.CycleTimeAddon;
import net.avdw.todo.extension.timing.LeadTimeAddon;
import net.avdw.todo.extension.timing.ReactionTimeAddon;

public class MixinModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<Mixin> addons = Multibinder.newSetBinder(binder(), Mixin.class);
        addons.addBinding().to(ProgressAddon.class);
        addons.addBinding().to(ChangeAddon.class);
        addons.addBinding().to(ReactionTimeAddon.class);
        addons.addBinding().to(CycleTimeAddon.class);
        addons.addBinding().to(LeadTimeAddon.class);
        addons.addBinding().to(StateAddon.class);
    }
}
