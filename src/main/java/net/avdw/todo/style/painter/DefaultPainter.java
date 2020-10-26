package net.avdw.todo.style.painter;

import net.avdw.todo.domain.Todo;

public class DefaultPainter implements IDefaultPainter {
    private final String regex;
    private final String color;

    public DefaultPainter(final String regex, final String color) {
        this.regex = regex;
        this.color = color;
    }

    @Override
    public boolean isFallback() {
        return regex.equals(".*");
    }

    @Override
    public String color() {
        return color;
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return todo.getText().matches(regex);
    }

    @Override
    public String paint(final String string, final String reset) {
        return String.format("%s%s", color, string);
    }
}
