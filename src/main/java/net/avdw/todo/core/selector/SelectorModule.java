package net.avdw.todo.core.selector;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import net.avdw.todo.extension.moscow.MoscowSelector;
import net.avdw.todo.extension.plan.PlanSelector;

@Module
public abstract class SelectorModule {

    @Binds
    @IntoSet
    abstract Selector contextSelector(ContextSelector contextSelector);

    @Binds
    @IntoSet
    abstract Selector projectSelector(ProjectSelector projectSelector);

    @Binds
    @IntoSet
    abstract Selector moscowSelector(MoscowSelector moscowSelector);

    @Binds
    @IntoSet
    abstract Selector planSelector(PlanSelector planSelector);


}
