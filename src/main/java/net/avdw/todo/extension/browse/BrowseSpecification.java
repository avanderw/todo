package net.avdw.todo.extension.browse;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.AbstractSpecification;

import javax.inject.Inject;
import java.util.regex.Matcher;

public class BrowseSpecification extends AbstractSpecification<Integer, Todo> {

    @Inject
    BrowseSpecification() {

    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        final Matcher matcher = BrowseStatic.PATTERN.matcher(todo.getText());
        return matcher.find();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
