package net.avdw.todo.core.style.painter;

public class RegexPainter implements IPainter {
    private final String color;
    private final String regex;

    public RegexPainter(final String regex, final String color) {
        this.regex = regex;
        this.color = color;
    }

    @Override
    public String paint(final String string, final String reset) {
        return string.replaceAll(regex, String.format("%s$0%s", color, reset));
    }
}
