package net.avdw.todo.extension.moscow;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.AbstractSpecification;

@Singleton
public class HasMoscow extends AbstractSpecification<Integer, Todo> {
    private final MoscowExt moscowExt;

    @Inject
    HasMoscow(final MoscowExt moscowExt) {
        this.moscowExt = moscowExt;
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return moscowExt.isSatisfiedBy(todo);
    }

    @Override
    public String toString() {
        return "hasMoscow";
    }
}
