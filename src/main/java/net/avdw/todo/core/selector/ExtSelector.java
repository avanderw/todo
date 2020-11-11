package net.avdw.todo.core.selector;

import net.avdw.todo.domain.RegexSpecification;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Specification;

import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExtSelector implements Selector {
    private final String ext;
    private final Pattern pattern;
    private final String regex;

    public ExtSelector(final String ext) {
        if (!ext.endsWith(":")) {
            throw new UnsupportedOperationException();
        }
        regex = ext + "(?!\\S)";
        this.ext = ext.substring(0, ext.length() - 1);
        pattern = Pattern.compile(regex);
    }

    @Override
    public Comparator<? super Todo> comparator() {
        return Comparator.comparing(todo -> todo.getExtValueList(ext).stream()
                .map(String::toLowerCase)
                .sorted()
                .collect(Collectors.joining()));
    }

    @Override
    public boolean isSatisfiedBy(final String type) {
        return pattern.matcher(type).find();
    }

    @Override
    public int mapToInt(final Todo todo) {
        return todo.getExtValueList(ext).stream().mapToInt(Integer::parseInt).sum();
    }

    @Override
    public String replaceRegex() {
        return regex;
    }

    @Override
    public Specification<Integer, Todo> specification() {
        return new RegexSpecification("\\S+:\\S+");
    }

    @Override
    public String symbol() {
        return ext;
    }

    @Override
    public String toString() {
        return "ExtSelector{" +
                "ext='" + ext + '\'' +
                '}';
    }
}
