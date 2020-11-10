package net.avdw.todo.core.selector;

import net.avdw.todo.domain.RegexSpecification;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Specification;

public class ContextSelector implements Selector {
    @Override
    public int intValue(final Todo todo) {
        return 0;
    }

    @Override
    public boolean isSatisfiedBy(final String type) {
        return type.contains("context");
    }

    @Override
    public String regex() {
        return "context";
    }

    @Override
    public Specification<Integer, Todo> specification() {
        return new RegexSpecification("@\\S+");
    }

    @Override
    public String symbol() {
        return "context";
    }
}
