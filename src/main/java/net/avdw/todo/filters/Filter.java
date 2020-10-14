package net.avdw.todo.filters;

import net.avdw.todo.repository.IdType;
import net.avdw.todo.repository.Specification;

public interface Filter<I, T extends IdType<I>> {
    Specification<I, T> specification(Specification<I, T> specification);
}
