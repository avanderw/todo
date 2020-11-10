package net.avdw.todo.core.selector;

import net.avdw.todo.domain.RegexSpecification;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Specification;

public class ProjectSelector implements Selector {
    @Override
    public int intValue(final Todo todo) {
        return 0;
    }

    @Override
    public boolean isSatisfiedBy(final String type) {
        return type.contains("project");
    }

    @Override
    public String regex() {
        return "project";
    }

    @Override
    public Specification<Integer, Todo> specification() {
        return new RegexSpecification("\\+\\S+");
    }

    @Override
    public String symbol() {
        return "project";
    }
}
