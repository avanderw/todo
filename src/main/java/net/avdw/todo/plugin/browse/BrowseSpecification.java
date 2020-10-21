package net.avdw.todo.plugin.browse;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.AbstractSpecification;

import com.google.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
