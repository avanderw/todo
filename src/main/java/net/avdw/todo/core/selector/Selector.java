package net.avdw.todo.core.selector;

import net.avdw.todo.Guard;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Specification;

import java.util.Comparator;

public interface Selector extends Guard<String> {
    Comparator<? super Todo> comparator();

    int mapToInt(Todo todo);

    String replaceRegex();

    Specification<Integer, Todo> specification();

    String symbol();
}
