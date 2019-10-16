package net.avdw.todo.theme;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class ThemeApplicator {

    private final int lineLength;
    private final ColorTheme colorTheme;

    @Inject
    ThemeApplicator(@LineLength final int lineLength, final ColorTheme colorTheme) {
        this.lineLength = lineLength;
        this.colorTheme = colorTheme;
    }


    public String h1(final String text) {
        return StringUtils.center(String.format("< %s >", text), lineLength, "-");
    }

    public String h2(final String text) {
        return StringUtils.center(String.format("[ %s ]", text), lineLength, "-");
    }

    public String hr() {
        return StringUtils.repeat("-", lineLength);
    }

    public String selected(final String text) {
        return String.format("%s%s%s", colorTheme.selected(), text, colorTheme.txt());
    }

    public String txt(final String text) {
        return String.format("%s%s", colorTheme.txt(), text);
    }

    public String blockComplete() {
        return String.format("%s %s", colorTheme.blockComplete(), colorTheme.txt());
    }

    public String blockIncomplete() {
        return String.format("%s %s", colorTheme.blockIncomplete(), colorTheme.txt());
    }

    public String context(final String context) {
        return String.format("%s%s%s", colorTheme.context(), context, colorTheme.txt());
    }
    public String project(final String context) {
        return String.format("%s%s%s", colorTheme.project(), context, colorTheme.txt());
    }
}
