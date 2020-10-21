package net.avdw.todo.plugin.change;

import net.avdw.todo.ThrowingFunction;
import net.avdw.todo.domain.Todo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChangeMapper {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Optional<Date> mapToLastChangeDate(final Todo todo) {
        List<Date> changeDateList = new ArrayList<>();
        if (todo.getAdditionDate() != null) {
            changeDateList.add(todo.getAdditionDate());
        }

        if (todo.isDone()) {
            changeDateList.add(todo.getDoneDate());
        } else if (todo.isRemoved()) {
            changeDateList.add(todo.getRemovedDate());
        } else if (todo.isParked()) {
            changeDateList.add(todo.getParkedDate());
        }

        if (!todo.getTagValueList("started").isEmpty()) {
            changeDateList.addAll(todo.getTagValueList("started").stream()
                    .map(ThrowingFunction.unchecked(s -> simpleDateFormat.parse(s)))
                    .collect(Collectors.toList()));
        }

        return changeDateList.stream().max(Comparator.naturalOrder());
    }
}
