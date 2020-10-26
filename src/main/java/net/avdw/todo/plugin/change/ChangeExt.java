package net.avdw.todo.plugin.change;

import net.avdw.todo.ThrowingFunction;
import net.avdw.todo.plugin.Ext;
import net.avdw.todo.domain.Todo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChangeExt implements Ext<Change> {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final List<String> supportedExtList = new ArrayList<>();

    ChangeExt() {
        supportedExtList.add("started");
    }

    @Override
    public List<Change> getValueList(final Todo todo) {
        return supportedExtList.stream()
                .flatMap(ext -> todo.getTagValueList(ext).stream()
                        .map(ThrowingFunction.unchecked(simpleDateFormat::parse))
                        .filter(Objects::nonNull)
                        .map(value -> new Change(ext, value)))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getSupportedExtList() {
        return supportedExtList;
    }

    @Override
    public Optional<Change> getValue(final Todo todo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return !supportedExtList.stream().allMatch(ext -> todo.getTagValueList(ext).isEmpty());
    }
}
