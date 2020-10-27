package net.avdw.todo.plugin.muscow;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.domain.Todo;

@Singleton
public class MoscowMapper {

    private final MoscowExt moscowExt;

    @Inject
    MoscowMapper(final MoscowExt moscowExt) {
        this.moscowExt = moscowExt;
    }

    public String map(final Todo todo) {
        return moscowExt.getValue(todo).orElse("No");
    }
}
