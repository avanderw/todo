package net.avdw.todo.plugin.link;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.plugin.Ext;
import net.avdw.todo.repository.AbstractSpecification;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LinkExt extends AbstractSpecification<Integer, Todo> implements Ext<String> {
    private final List<String> supportedExtList = new ArrayList<>();

    @Inject
    LinkExt() {
        supportedExtList.add("link");
    }

    @Override
    public List<String> getSupportedExtList() {
        return supportedExtList;
    }

    @Override
    public Optional<String> getValue(final Todo todo) {
        List<String> valueList = getValueList(todo);
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
        List<String> extValueList = supportedExtList.stream()
                .flatMap(ext -> todo.getTagValueList(ext).stream())
                .collect(Collectors.toList());

        if (extValueList.size() > 1) {
            Logger.warn("multiple link tags found for {}", todo.getText());
        }

        return extValueList;
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return !supportedExtList.stream().allMatch(ext -> todo.getTagValueList(ext).isEmpty());
    }

    @Override
    public String preferredExt() {
        return supportedExtList.get(0);
    }
}
