package net.avdw.todo.core.selector;

import net.avdw.todo.domain.RegexSpecification;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Specification;

import java.util.regex.Pattern;

public class SpecificContextSelector implements Selector {
    private final String regex = "@\\S+";
    private final Pattern pattern = Pattern.compile(regex);

    @Override
    public int intValue(final Todo todo) {
        return 0;
    }

    @Override
    public boolean isSatisfiedBy(final String type) {
        return pattern.matcher(type).find();
    }

    @Override
    public String regex() {
        return regex;
    }

    @Override
    public Specification<Integer, Todo> specification() {
        return new RegexSpecification(regex);
    }

    @Override
    public String symbol() {
        return "context";
    }
}
