package net.avdw.todo.plugin.progress;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;

import java.util.ArrayList;
import java.util.List;

public class ProgressExt {
    private final List<String> supportedExtList = new ArrayList<>();

    @Inject
    ProgressExt() {
        supportedExtList.add("started");
    }

    public boolean notStarted(final Todo todo) {
        return !todo.isDone() && todo.getTagValueList("started").isEmpty();
    }

    public boolean started(final Todo todo) {
        return !todo.isDone() && !todo.getTagValueList("started").isEmpty();
    }
}
