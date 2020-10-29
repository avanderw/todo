package net.avdw.todo.plugin.timing;

import net.avdw.todo.ThrowingFunction;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.plugin.Ext;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TimingExt implements Ext<Date> {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final List<String> supportedExtList = new ArrayList<>();

    TimingExt() {
        supportedExtList.add("started");
    }

    @Override
    public List<String> getSupportedExtList() {
        return supportedExtList;
    }

    @Override
    public Optional<Date> getValue(final Todo todo) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Date> getValueList(final Todo todo) {
        return supportedExtList.stream()
                .flatMap(ext -> todo.getTagValueList(ext).stream()
                        .map(ThrowingFunction.unchecked(simpleDateFormat::parse))
                        .filter(Objects::nonNull))
                .collect(Collectors.toList());
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