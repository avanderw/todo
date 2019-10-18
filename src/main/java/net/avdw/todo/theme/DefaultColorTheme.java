package net.avdw.todo.theme;

import com.google.inject.Inject;
import net.avdw.todo.color.ColorConverter;

public class DefaultColorTheme implements ColorTheme {
    private final ColorPalette colorPalette;
    private final ColorConverter colorConverter;

    @Inject
    DefaultColorTheme(final ColorPalette colorPalette, final ColorConverter colorConverter) {
        this.colorPalette = colorPalette;
        this.colorConverter = colorConverter;
    }

    @Override
    public String selected() {
        return colorConverter.hexToAnsiFg(colorPalette.accentTone(), false);
    }

    @Override
    public String txt() {
        return colorConverter.hexToAnsiFg(colorPalette.primaryTone(), false);
    }

    @Override
    public String blockComplete() {
        return colorConverter.hexToAnsiBg(colorPalette.primaryTint());
    }

    @Override
    public String blockIncomplete() {
        return colorConverter.hexToAnsiBg(colorPalette.primaryShade());
    }

    @Override
    public String context() {
        return colorConverter.hexToAnsiFg(colorPalette.secondaryTone(), false);
    }

    @Override
    public String project() {
        return colorConverter.hexToAnsiFg(colorPalette.primaryTint(), false);
    }

    @Override
    public int progressStart() {
        return colorPalette.primaryShade();
    }

    @Override
    public int progressEnd() {
        return colorPalette.primaryTint();
    }
}
