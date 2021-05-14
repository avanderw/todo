package net.avdw.todo.core.groupby;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.moscow.MoscowGroup;
import net.avdw.todo.extension.plan.PlanGroup;
import net.avdw.todo.extension.state.StateGroup;

@Module
public abstract class GroupByModule {


    @Binds
    @IntoSet
    abstract Group<Todo, String> contextGroup(ContextGroup contextGroup);

    @Binds
    @IntoSet
    abstract Group<Todo, String> moscowGroup(MoscowGroup moscowGroup);

    @Binds
    @IntoSet
    abstract Group<Todo, String> projectGroup(ProjectGroup projectGroup);

    @Binds
    @IntoSet
    abstract Group<Todo, String> stateGroup(StateGroup stateGroup);

    @Binds
    @IntoSet
    abstract Group<Todo, String> planGroup(PlanGroup planGroup);

}
