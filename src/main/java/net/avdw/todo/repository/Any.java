package net.avdw.todo.repository;

public class Any<I, T extends IdType<I>> extends AbstractSpecification<I, T> {
    @Override
    public boolean isSatisfiedBy(final T todo) {
        return true;
    }
}
