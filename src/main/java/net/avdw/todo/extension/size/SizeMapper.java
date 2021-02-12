package net.avdw.todo.extension.size;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.domain.Todo;

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
