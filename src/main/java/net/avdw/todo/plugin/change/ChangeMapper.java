package net.avdw.todo.plugin.change;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ChangeMapper {
    private final ChangeExt changeExt;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Inject
    ChangeMapper(final ChangeExt changeExt) {
        this.changeExt = changeExt;
    }

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

        changeDateList.addAll(changeExt.getValueList(todo));

        return changeDateList.stream().max(Comparator.naturalOrder());
    }
}
