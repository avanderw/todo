package net.avdw.todo.plugin.moscow;

import com.google.inject.Inject;
import net.avdw.todo.core.groupby.Group;
import net.avdw.todo.domain.Todo;

import java.util.Locale;
import java.util.function.Function;

public class MoscowGroup implements Group<Todo, String> {
    private final MoscowExt moscowExt;
    private final MoscowMapper moscowMapper;

    @Inject
    public MoscowGroup(final MoscowMapper moscowMapper, final MoscowExt moscowExt) {
        this.moscowMapper = moscowMapper;
        this.moscowExt = moscowExt;
    }

    @Override
    public Function<Todo, String> collector() {
        return (moscowMapper::map);
    }

    @Override
    public boolean isSatisfiedBy(final String selector) {
        return moscowExt.getSupportedExtList().contains(selector.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public String name() {
        return "have";
    }
}
