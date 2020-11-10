package net.avdw.todo.core.selector;

import net.avdw.todo.domain.RegexSpecification;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Specification;

import java.util.regex.Pattern;

public class SpecificExtSelector implements Selector {
    private final String regex;
    private final String ext;
    private final Pattern pattern;

    public SpecificExtSelector(final String ext) {
        regex = ext;
        this.ext = ext;
        pattern = Pattern.compile(regex);
    }

    @Override
    public int intValue(final Todo todo) {
        return 0;
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
        return ext;
    }

    @Override
    public boolean isSatisfiedBy(final String type) {
        return pattern.matcher(type).find();
    }

    @Override
    public String toString() {
        return "SpecificExtSelector{" +
                "ext='" + ext + '\'' +
                '}';
    }
}
