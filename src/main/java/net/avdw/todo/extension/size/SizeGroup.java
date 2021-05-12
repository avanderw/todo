package net.avdw.todo.extension.size;

import net.avdw.todo.core.groupby.Group;
import net.avdw.todo.domain.Todo;

import javax.inject.Inject;
import java.util.Locale;
import java.util.function.Function;

public class SizeGroup implements Group<Todo, String> {
    private final SizeExt sizeExt;
    private final SizeMapper sizeMapper;

    @Inject
    public SizeGroup(final SizeMapper sizeMapper, final SizeExt sizeExt) {
        this.sizeMapper = sizeMapper;
        this.sizeExt = sizeExt;
    }

    @Override
    public Function<Todo, String> collector() {
        return sizeMapper::map;
    }

    @Override
    public boolean isSatisfiedBy(final String selector) {
        return sizeExt.getSupportedExtList().contains(selector.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public String name() {
        return "size";
    }
}
