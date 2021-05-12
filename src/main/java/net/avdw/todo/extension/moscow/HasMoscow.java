package net.avdw.todo.extension.moscow;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.AbstractSpecification;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HasMoscow extends AbstractSpecification<Integer, Todo> {
    private final MoscowTodoTxtExt moscowExt;

    @Inject
    HasMoscow(final MoscowTodoTxtExt moscowExt) {
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
