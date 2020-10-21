package net.avdw.todo.plugin.change;

import net.avdw.todo.domain.Todo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ChangeMapper {
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

        return changeDateList.stream().max(Comparator.naturalOrder());
    }
}
