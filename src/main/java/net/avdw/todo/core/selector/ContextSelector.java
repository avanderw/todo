package net.avdw.todo.core.selector;

import net.avdw.todo.domain.RegexSpecification;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Specification;

import java.util.Comparator;
import java.util.stream.Collectors;

public class ContextSelector implements Selector {
    @Override
    public Comparator<? super Todo> comparator() {
        return Comparator.comparing((Todo todo) -> todo.getContextList().stream()
                .map(String::toLowerCase)
                .sorted()
                .collect(Collectors.joining()));
    }

    @Override
    public boolean isSatisfiedBy(final String type) {
        return type.contains("context");
    }

    @Override
    public int mapToInt(final Todo todo) {
        return 0;
    }

    @Override
    public String replaceRegex() {
        return "context";
    }

    @Override
    public Specification<Integer, Todo> specification() {
        return new RegexSpecification("@\\S+");
    }

    @Override
    public String symbol() {
        return "ctx";
    }
}
