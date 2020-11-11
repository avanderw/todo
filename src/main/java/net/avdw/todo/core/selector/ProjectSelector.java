package net.avdw.todo.core.selector;

import net.avdw.todo.domain.RegexSpecification;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Specification;

import java.util.Comparator;
import java.util.stream.Collectors;

public class ProjectSelector implements Selector {
    @Override
    public Comparator<? super Todo> comparator() {
        return Comparator.comparing(todo -> todo.getProjectList().stream()
                .map(String::toLowerCase)
                .sorted()
                .collect(Collectors.joining()));
    }

    @Override
    public int mapToInt(final Todo todo) {
        return 0;
    }

    @Override
    public boolean isSatisfiedBy(final String type) {
        return type.contains("project");
    }

    @Override
    public String replaceRegex() {
        return "project";
    }

    @Override
    public Specification<Integer, Todo> specification() {
        return new RegexSpecification("\\+\\S+");
    }

    @Override
    public String symbol() {
        return "prj";
    }
}
