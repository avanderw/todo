package net.avdw.todo.extension.size;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.AbstractSpecification;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HasSize extends AbstractSpecification<Integer, Todo> {
    private final SizeExt sizeExt;

    @Inject
    HasSize(final SizeExt sizeExt) {
        this.sizeExt = sizeExt;
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return sizeExt.isSatisfiedBy(todo);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
