package net.avdw.todo.theme;

import com.google.inject.Inject;
import net.avdw.todo.color.ColorConverter;
import net.avdw.todo.color.ColorInterpolator;
import net.avdw.todo.number.Interpolation;
import org.apache.commons.lang3.StringUtils;

public class ThemeApplicator {

    private final int lineLength;
    private final ColorTheme colorTheme;
    private final ColorConverter colorConverter;
    private final ColorInterpolator colorInterpolator;

    @Inject
    ThemeApplicator(@LineLength final int lineLength, final ColorTheme colorTheme, final ColorConverter colorConverter, final ColorInterpolator colorInterpolator) {
        this.lineLength = lineLength;
        this.colorTheme = colorTheme;
        this.colorConverter = colorConverter;
        this.colorInterpolator = colorInterpolator;
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

    public String progress(final String text, final Double progress) {
        int color = colorInterpolator.interpolate(colorTheme.progressStart(), colorTheme.progressEnd(), progress, Interpolation.LINEAR);
        String ansiColor = colorConverter.hexToAnsiFg(color, false);
        return String.format("%s%s%s", ansiColor, text, colorTheme.txt());
    }
}
