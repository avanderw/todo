package net.avdw.todo.theme;

import com.google.inject.Inject;
import net.avdw.todo.AnsiColor;

public class DefaultColorTheme implements ColorTheme {
    private final ColorPalette colorPalette;
    private final AnsiColor ansiColor;

    @Inject
    DefaultColorTheme(final ColorPalette colorPalette, final AnsiColor ansiColor) {
        this.colorPalette = colorPalette;
        this.ansiColor = ansiColor;
    }

    @Override
    public String em() {
        return ansiColor.getForegroundColor(colorPalette.primaryTint(), false);
    }

    @Override
    public String txt() {
        return ansiColor.getForegroundColor(colorPalette.primaryTone(), false);
    }

    @Override
    public String blockComplete() {
        return ansiColor.getBackgroundColor(colorPalette.primaryTint());
    }

    @Override
    public String blockIncomplete() {
        return ansiColor.getBackgroundColor(colorPalette.primaryShade());
    }
}
