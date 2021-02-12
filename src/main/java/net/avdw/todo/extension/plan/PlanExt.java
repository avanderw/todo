package net.avdw.todo.extension.plan;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.TodoTxtExt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlanExt implements TodoTxtExt<String> {
    private final List<String> supportedExtList = new ArrayList<>();

    PlanExt() {
        supportedExtList.add("plan");
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
        } else if (valueList.size() > 1) {
            throw new UnsupportedOperationException();
        } else {
            return Optional.of(valueList.get(0));
        }
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
}
