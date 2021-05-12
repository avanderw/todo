package net.avdw.todo.core;

import dagger.Module;
import net.avdw.todo.core.groupby.GroupByModule;
import net.avdw.todo.core.selector.SelectorModule;

@Module(includes = {GroupByModule.class, SelectorModule.class})
public abstract class CoreModule {
}
