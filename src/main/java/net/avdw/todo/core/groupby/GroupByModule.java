package net.avdw.todo.core.groupby;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.change.ChangeTypeGroup;
import net.avdw.todo.extension.moscow.MoscowGroup;
import net.avdw.todo.extension.plan.PlanGroup;
import net.avdw.todo.extension.state.StateGroup;

public class GroupByModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<Group<Todo, String>> addons = Multibinder.newSetBinder(binder(), new TypeLiteral<>() {
        });
        addons.addBinding().to(ChangeTypeGroup.class);
        addons.addBinding().to(ContextGroup.class);
        addons.addBinding().to(MonthGroup.class);
        addons.addBinding().to(MoscowGroup.class);
        addons.addBinding().to(ProjectGroup.class);
        addons.addBinding().to(StateGroup.class);
        addons.addBinding().to(PlanGroup.class);
    }
}
