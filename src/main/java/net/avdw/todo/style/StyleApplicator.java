package net.avdw.todo.style;

import com.google.inject.Inject;
import net.avdw.todo.style.painter.IPainter;
import org.fusesource.jansi.Ansi;

import java.util.List;

public class StyleApplicator {

    private final DefaultTextColor defaultTextColor;
    private final List<IPainter> stylerList;

    @Inject
    StyleApplicator(final DefaultTextColor defaultTextColor, final List<IPainter> stylerList) {
        this.defaultTextColor = defaultTextColor;
        this.stylerList = stylerList;
    }

    public String apply(final String text) {
        String styledText = text;
        for (IPainter styler : stylerList) {
            styledText = styler.paint(styledText, "<reset>");
        }
        return Ansi.ansi().a(defaultTextColor.getFromText(text)).a(styledText).reset().toString();
    }
}
