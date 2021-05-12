package net.avdw.todo.extension.progress;

import net.avdw.todo.domain.Todo;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class ProgressExt {
    private final List<String> supportedExtList = new ArrayList<>();

    @Inject
    ProgressExt() {
        supportedExtList.add("started");
    }

    public boolean notStarted(final Todo todo) {
        return !todo.isDone() && todo.getExtValueList("started").isEmpty();
    }

    public boolean started(final Todo todo) {
        return !todo.isDone() && !todo.getExtValueList("started").isEmpty();
    }
}
