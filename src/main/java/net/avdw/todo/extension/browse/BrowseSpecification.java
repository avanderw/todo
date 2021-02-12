package net.avdw.todo.extension.browse;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.AbstractSpecification;

import java.util.regex.Matcher;

public class BrowseSpecification extends AbstractSpecification<Integer, Todo> {

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        Matcher matcher = BrowseStatic.PATTERN.matcher(todo.getText());
        return matcher.find();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
