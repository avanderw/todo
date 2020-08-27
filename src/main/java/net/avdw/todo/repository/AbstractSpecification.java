package net.avdw.todo.repository;

import java.lang.reflect.ParameterizedType;

abstract public class AbstractSpecification<T> implements Specification<T> {
    @Override
    public boolean isSatisfiedBy(final T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Specification<T> and(final Specification<T> other) {
        return new AndSpecification<>(this, other);
    }

    @Override
    public Specification<T> or(final Specification<T> other) {
        return new OrSpecification<>(this, other);
    }

    @Override
    public Specification<T> not(final Specification<T> other) {
        return new NotSpecification<>(this, other);
    }

    @Override
    public Class<T> getType() {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }
}
