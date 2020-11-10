package net.avdw.todo.plugin.blocker;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.plugin.Ext;
import net.avdw.todo.repository.AbstractSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BlockerExt extends AbstractSpecification<Integer, Todo> implements Ext<String> {
    private final List<String> supportedExtList = new ArrayList<>();

    BlockerExt() {
        supportedExtList.add("blocker");
    }

    @Override
    public List<String> getSupportedExtList() {
        return supportedExtList;
    }

    @Override
    public Optional<String> getValue(final Todo todo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getValueList(final Todo todo) {
        return supportedExtList.stream()
                .flatMap(ext -> todo.getExtValueList(ext).stream())
                .collect(Collectors.toList());
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return !supportedExtList.stream().allMatch(ext -> todo.getExtValueList(ext).isEmpty());
    }

    @Override
    public String preferredExt() {
        return supportedExtList.get(0);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
