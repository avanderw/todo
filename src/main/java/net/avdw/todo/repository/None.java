package net.avdw.todo.repository;

public class None<I, T extends IdType<I>> extends AbstractSpecification<I, T> {
    @Override
    public boolean isSatisfiedBy(final T todo) {
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
