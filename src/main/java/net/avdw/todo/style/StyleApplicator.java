package net.avdw.todo.style;

import com.google.inject.Inject;
import org.fusesource.jansi.Ansi;

import java.util.List;

public class StyleApplicator {

    private final DefaultTextColor defaultTextColor;
    private final List<IStyler> stylerList;

    @Inject
    StyleApplicator(final DefaultTextColor defaultTextColor, final List<IStyler> stylerList) {
        this.defaultTextColor = defaultTextColor;
        this.stylerList = stylerList;
    }

    public String apply(final String text) {
        String styledText = text;
        for (IStyler styler : stylerList) {
            styledText = styler.style(styledText);
        }
        return Ansi.ansi().a(defaultTextColor.getFromText(text)).a(styledText).reset().toString();
    }
}
