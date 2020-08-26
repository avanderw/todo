package net.avdw.todo.repository;

public class AndSpecification<T> extends AbstractSpecification<T> {
    private final Specification<T> first;
    private final Specification<T> second;

    public AndSpecification(final Specification<T> first, final Specification<T> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean isSatisfiedBy(final T t) {
        return first.isSatisfiedBy(t) && second.isSatisfiedBy(t);
    }

    @Override
    public Class<T> getType() {
        return first.getType();
    }
}
