package net.avdw.todo.repository;

public class NotSpecification<I, T extends IdType<I>> extends AbstractSpecification<I, T> {
    private final Specification<I, T> first;
    private final Specification<I, T> second;

    public NotSpecification(final Specification<I, T> first, final Specification<I, T> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean isSatisfiedBy(final T t) {
        return first.isSatisfiedBy(t) && !second.isSatisfiedBy(t);
    }

    @Override
    public Class<T> getType() {
        return first.getType();
    }
}
