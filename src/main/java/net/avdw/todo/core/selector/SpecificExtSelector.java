package net.avdw.todo.core.selector;

import net.avdw.todo.domain.RegexSpecification;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Specification;

import java.util.Comparator;
import java.util.regex.Pattern;

public class SpecificExtSelector implements Selector {
    private final String ext;
    private final Pattern pattern;
    private final String regex;

    public SpecificExtSelector(final String ext) {
        regex = ext;
        this.ext = ext;
        pattern = Pattern.compile(regex);
    }

    @Override
    public Comparator<? super Todo> comparator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSatisfiedBy(final String type) {
        return pattern.matcher(type).find();
    }

    @Override
    public int mapToInt(final Todo todo) {
        return 0;
    }

    @Override
    public String replaceRegex() {
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
    public String toString() {
        return "SpecificExtSelector{" +
                "ext='" + ext + '\'' +
                '}';
    }
}
