package net.avdw.todo.extension;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import net.avdw.todo.extension.change.ChangeAddon;
import net.avdw.todo.extension.progress.ProgressAddon;
import net.avdw.todo.extension.recur.RecurDonePostAddon;
import net.avdw.todo.extension.state.StateAddon;
import net.avdw.todo.extension.timing.CycleTimeAddon;
import net.avdw.todo.extension.timing.LeadTimeAddon;
import net.avdw.todo.extension.timing.ReactionTimeAddon;

@Module
public abstract class ExtensionModule {
    @Binds
    @IntoSet
    abstract Mixin progressAddon(ProgressAddon progressAddon);

    @Binds
    @IntoSet
    abstract Mixin changeAddon(ChangeAddon changeAddon);

    @Binds
    @IntoSet
    abstract Mixin reactionTimeAddon(ReactionTimeAddon reactionTimeAddon);

    @Binds
    @IntoSet
    abstract Mixin cycleTimeAddon(CycleTimeAddon cycleTimeAddon);

    @Binds
    @IntoSet
    abstract Mixin leadTimeAddon(LeadTimeAddon leadTimeAddon);

    @Binds
    @IntoSet
    abstract Mixin stateAddon(StateAddon stateAddon);

    @Binds
    @IntoSet
    abstract PostAddon recurDonePostAddon(RecurDonePostAddon recurDonePostAddon);
}
