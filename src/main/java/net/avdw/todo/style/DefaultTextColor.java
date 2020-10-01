package net.avdw.todo.style;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;

public class DefaultTextColor {
    private final String defaultColor;
    private final String defaultDoneColor;
    private final String defaultRemovedColor;
    private final String defaultParkedColor;

    @Inject
    DefaultTextColor(@DefaultColor final String defaultColor,
                     @DefaultDoneColor final String defaultDoneColor,
                     @DefaultRemovedColor final String defaultRemovedColor,
                     @DefaultParkedColor final String defaultParkedColor) {
        this.defaultColor = defaultColor;
        this.defaultDoneColor = defaultDoneColor;
        this.defaultRemovedColor = defaultRemovedColor;
        this.defaultParkedColor = defaultParkedColor;
    }

    public String getFromText(final String text) {
        Todo todo = new Todo(-1, text);
        String defaultTextColor = defaultColor;
        if (todo.isDone()) {
            defaultTextColor = defaultDoneColor;
        }
        if (todo.isRemoved()) {
            defaultTextColor = defaultRemovedColor;
        }
        if (todo.isParked()) {
            defaultTextColor = defaultParkedColor;
        }
        return defaultTextColor;
    }
}
