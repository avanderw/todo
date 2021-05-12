package net.avdw.todo.extension.size;

import net.avdw.todo.domain.Todo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SizeMapper {

    private final SizeExt sizeExt;

    @Inject
    SizeMapper(final SizeExt sizeExt) {
        this.sizeExt = sizeExt;
    }

    public String map(final Todo todo) {
        return sizeExt.getValue(todo).orElse("No");
    }
}
