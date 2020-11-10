package net.avdw.todo.core.selector;

import net.avdw.todo.Guard;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Specification;

public interface Selector extends Guard<String> {
    int intValue(final Todo todo);

    String regex();

    Specification<Integer, Todo> specification();

    String symbol();
}
