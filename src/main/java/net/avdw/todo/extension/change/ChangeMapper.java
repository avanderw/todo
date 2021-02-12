package net.avdw.todo.extension.change;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;

import java.util.Comparator;

public class ChangeMapper {
    private final ChangeExt changeExt;

    @Inject
    public ChangeMapper(final ChangeExt changeExt) {
        this.changeExt = changeExt;
    }

    public Change mapToChange(final Todo todo) {
        Change change = null;
        if (todo.isDone()) {
            change = new Change("Done", todo.getDoneDate());
        } else if (todo.isRemoved()) {
            change = new Change("Removed", todo.getRemovedDate());
        } else if (todo.isParked()) {
            change = new Change("Parked", todo.getParkedDate());
        } else if (changeExt.isSatisfiedBy(todo)) {
            change = changeExt.getValueList(todo).stream().max(Comparator.comparing(Change::getDate)).orElseThrow();
        } else if (todo.getAdditionDate() != null) {
            change = new Change("Added", todo.getAdditionDate());
        } else {
            change = new Change("No group", null);
        }

        return change;
    }
}
