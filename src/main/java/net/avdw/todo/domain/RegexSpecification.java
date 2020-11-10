package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;

import java.util.regex.Pattern;

public class RegexSpecification extends AbstractSpecification<Integer, Todo> {
    private final Pattern pattern;

    public RegexSpecification(final String regex) {
        pattern = Pattern.compile(regex);
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return pattern.matcher(todo.getText()).find();
    }
}
