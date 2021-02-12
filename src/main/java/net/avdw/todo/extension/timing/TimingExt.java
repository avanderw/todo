package net.avdw.todo.extension.timing;

import net.avdw.todo.ThrowingFunction;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.TodoTxtExt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TimingExt implements TodoTxtExt<Date> {
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
                .flatMap(ext -> todo.getExtValueList(ext).stream()
                        .map(ThrowingFunction.unchecked(simpleDateFormat::parse))
                        .filter(Objects::nonNull))
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
}
