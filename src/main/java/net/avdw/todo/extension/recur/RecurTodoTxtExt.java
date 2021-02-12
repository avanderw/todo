package net.avdw.todo.extension.recur;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.TodoTxtExt;
import org.tinylog.Logger;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecurTodoTxtExt implements TodoTxtExt<RecurDuration> {
    private static final List<String> supportedExtList = Arrays.asList("rec", "recur");

    @Override
    public List<String> getSupportedExtList() {
        return supportedExtList;
    }

    @Override
    public Optional<RecurDuration> getValue(final Todo todo) {
        List<RecurDuration> valueList = getValueList(todo);
        if (valueList.isEmpty()) {
            return Optional.empty();
        } else if (valueList.size() > 1) {
            throw new UnsupportedOperationException();
        } else {
            return Optional.of(valueList.get(0));
        }
    }

    @Override
    public List<RecurDuration> getValueList(final Todo todo) {
        return supportedExtList.stream().flatMap(ext-> todo.getExtValueList(ext).stream()).map(RecurDuration::new).collect(Collectors.toList());
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return supportedExtList.stream().anyMatch(ext -> !todo.getExtValueList(ext).isEmpty());
    }

    @Override
    public String preferredExt() {
        return "rec";
    }
}
