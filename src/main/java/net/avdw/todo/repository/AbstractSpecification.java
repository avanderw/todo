package net.avdw.todo.repository;

import java.lang.reflect.ParameterizedType;

abstract public class AbstractSpecification<I, T extends IdType<I>> implements Specification<I, T> {
    @Override
    public boolean isSatisfiedBy(final T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Specification<I, T> and(final Specification<I, T> other) {
        return new AndSpecification<>(this, other);
    }

    @Override
    public Specification<I, T> or(final Specification<I, T> other) {
        return new OrSpecification<>(this, other);
    }

    @Override
    public Specification<I, T> not(final Specification<I, T> other) {
        return new NotSpecification<>(this, other);
    }

    @Override
    public Class<T> getType() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }
}
