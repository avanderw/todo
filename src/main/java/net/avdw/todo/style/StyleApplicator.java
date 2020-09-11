package net.avdw.todo.style;

import com.google.inject.Inject;
import org.fusesource.jansi.Ansi;

import java.util.List;

public class StyleApplicator {

    private final String defaultColor;
    private final List<IStyler> stylerList;

    @Inject
    StyleApplicator(final String defaultColor, final List<IStyler> stylerList) {
        this.defaultColor = defaultColor;
        this.stylerList = stylerList;
    }

    public String apply(final String text) {
        String styledText = text;
        for (IStyler styler : stylerList) {
            styledText = styler.style(styledText);
        }
        return Ansi.ansi().a(defaultColor).a(styledText).reset().toString();
    }
}
