package net.avdw.todo.extension.due;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.TodoTxtExt;
import org.tinylog.Logger;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DueTodoTxtExt implements TodoTxtExt<Date> {
    private static final List<String> SUPPORTED_EXT_LIST = Collections.singletonList("due");

    @Inject
    DueTodoTxtExt() {
    }

    @Override
    public List<String> getSupportedExtList() {
        return SUPPORTED_EXT_LIST;
    }

    @Override
    public Optional<Date> getValue(final Todo todo) {
        final List<Date> valueList = getValueList(todo);
        if (valueList.isEmpty()) {
            return Optional.empty();
        } else if (valueList.size() > 1) {
            throw new UnsupportedOperationException();
        } else {
            return Optional.of(valueList.get(0));
        }
    }

    @Override
    public List<Date> getValueList(final Todo todo) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return SUPPORTED_EXT_LIST.stream().flatMap(ext -> todo.getExtValueList(ext).stream()).map(date -> {
            try {
                return simpleDateFormat.parse(date);
            } catch (final ParseException e) {
                Logger.error(e.getMessage());
                Logger.debug(e.getStackTrace());
                throw new UnsupportedOperationException();
            }
        }).collect(Collectors.toList());
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return todo.getText().contains("due:");
    }

    @Override
    public String preferredExt() {
        return "due";
    }
}
