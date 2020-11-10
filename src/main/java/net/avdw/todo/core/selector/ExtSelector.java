package net.avdw.todo.core.selector;

import net.avdw.todo.domain.RegexSpecification;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Specification;

import java.util.regex.Pattern;

public class ExtSelector implements Selector {
    private final String regex;
    private final String ext;
    private final Pattern pattern;

    public ExtSelector(final String ext) {
        if (!ext.endsWith(":")) {
            throw new UnsupportedOperationException();
        }
        regex = ext + "(?!\\S)";
        this.ext = ext.substring(0, ext.length() - 1);
        pattern = Pattern.compile(regex);
    }

    @Override
    public int intValue(final Todo todo) {
        return todo.getExtValueList(ext).stream().mapToInt(Integer::parseInt).sum();
    }

    @Override
    public String regex() {
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
    public boolean isSatisfiedBy(final String type) {
        return pattern.matcher(type).find();
    }

    @Override
    public String toString() {
        return "ExtSelector{" +
                "ext='" + ext + '\'' +
                '}';
    }
}
