package net.avdw.todo.plugin.progress;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;

public class ProgressExtension {
    private final String tag;

    @Inject
    public ProgressExtension(String tag) {
        this.tag = tag;
    }

    public boolean notStarted(final Todo todo) {
        return !todo.isDone() && todo.getTagValueList(tag).isEmpty();
    }

    public boolean started(final Todo todo) {
        return !todo.isDone() && !todo.getTagValueList(tag).isEmpty();
    }
}
