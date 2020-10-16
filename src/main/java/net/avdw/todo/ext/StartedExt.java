package net.avdw.todo.ext;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;

public class StartedExt {
    private final String tag;

    @Inject
    public StartedExt(String tag) {
        this.tag = tag;
    }

    public boolean notStarted(final Todo todo) {
        return !todo.isDone() && todo.getTagValueList(tag).isEmpty();
    }

    public boolean started(final Todo todo) {
        return !todo.isDone() && !todo.getTagValueList(tag).isEmpty();
    }
}
