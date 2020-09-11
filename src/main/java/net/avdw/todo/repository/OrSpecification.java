package net.avdw.todo.repository;

public class OrSpecification<I, T extends IdType<I>> extends AbstractSpecification<I, T> {
    private final Specification<I, T> first;
    private final Specification<I, T> second;

    public OrSpecification(final Specification<I, T> first, final Specification<I, T> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean isSatisfiedBy(final T t) {
        return first.isSatisfiedBy(t) || second.isSatisfiedBy(t);
    }

    @Override
    public Class<T> getType() {
        return first.getType();
    }

    @Override
    public String toString() {
        return String.format("(%s) || (%s)", first, second);
    }
}
