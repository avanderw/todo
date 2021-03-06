package net.avdw.todo.extension.dependency;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.TodoTxtExt;
import net.avdw.todo.repository.AbstractSpecification;
import org.tinylog.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DependencyExt extends AbstractSpecification<Integer, Todo> implements TodoTxtExt<String> {
    private final List<String> supportedExtList = new ArrayList<>();

    @Inject
    DependencyExt() {
        supportedExtList.add("link");
    }

    @Override
    public List<String> getSupportedExtList() {
        return supportedExtList;
    }

    @Override
    public Optional<String> getValue(final Todo todo) {
        final List<String> valueList = getValueList(todo);
        if (valueList.isEmpty()) {
            return Optional.empty();
        } else {
            if (valueList.size() > 1) {
                throw new UnsupportedOperationException();
            }

            return Optional.of(valueList.get(0));
        }
    }

    @Override
    public List<String> getValueList(final Todo todo) {
        final List<String> extValueList = supportedExtList.stream()
                .flatMap(ext -> todo.getExtValueList(ext).stream())
                .collect(Collectors.toList());

        if (extValueList.size() > 1) {
            Logger.warn("multiple link tags found for {}", todo.getText());
        }

        return extValueList;
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return !supportedExtList.stream().allMatch(ext -> todo.getExtValueList(ext).isEmpty());
    }

    @Override
    public String preferredExt() {
        return supportedExtList.get(0);
    }
}
