package net.avdw.todo.core.mixin;

import net.avdw.todo.repository.IdType;
import net.avdw.todo.repository.Specification;

public interface Filter<I, T extends IdType<I>> {
    Specification<I, T> specification();
}
