package net.avdw.todo.repository;

/**
 * https://dzone.com/articles/java-using-specification
 *
 * @version 2020-08-26: Basic setup as suggested in the article
 * @see AbstractSpecification
 */
public interface Specification<I, T extends IdType<I>> {
    boolean isSatisfiedBy(T t);

    Specification<I, T> and(Specification<I, T> other);

    Specification<I, T> or(Specification<I, T> other);

    Specification<I, T> not(Specification<I, T> other);

    Class<T> getType();
}
