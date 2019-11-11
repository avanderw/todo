package net.avdw.todo.theme;

import com.google.inject.Inject;
import net.avdw.todo.color.ColorConverter;

public class DefaultColorTheme implements ColorTheme {
    private final ColorPalette colorPalette;
    private final String ansiPrimaryTintFg;
    private final String ansiPrimaryToneFg;
    private final String ansiPrimaryShadeFg;
    private final String ansiSecondaryToneFg;
    private final String ansiAccentToneFg;
    private final String ansiPrimaryTintBg;
    private final String ansiPrimaryShadeBg;

    @Inject
    DefaultColorTheme(final ColorPalette colorPalette, final ColorConverter colorConverter) {
        this.colorPalette = colorPalette;

        ansiPrimaryTintFg = colorConverter.hexToAnsiFg(colorPalette.primaryTint(), false);
        ansiPrimaryToneFg = colorConverter.hexToAnsiFg(colorPalette.primaryTone(), false);
        ansiPrimaryShadeFg = colorConverter.hexToAnsiFg(colorPalette.primaryShade(), false);
        ansiSecondaryToneFg = colorConverter.hexToAnsiFg(colorPalette.secondaryTone(), false);
        ansiAccentToneFg = colorConverter.hexToAnsiFg(colorPalette.accentTone(), false);

        ansiPrimaryTintBg = colorConverter.hexToAnsiBg(colorPalette.primaryTint());
        ansiPrimaryShadeBg = colorConverter.hexToAnsiBg(colorPalette.primaryShade());
    }

    @Override
    public String selected() {
        return ansiAccentToneFg;
    }

    @Override
    public String txt() {
        return ansiPrimaryToneFg;
    }

    @Override
    public String blockComplete() {
        return ansiPrimaryTintBg;
    }

    @Override
    public String blockIncomplete() {
        return ansiPrimaryShadeBg;
    }

    @Override
    public String context() {
        return ansiSecondaryToneFg;
    }

    @Override
    public String project() {
        return ansiPrimaryTintFg;
    }

    @Override
    public int progressStart() {
        return colorPalette.primaryShade();
    }

    @Override
    public int progressEnd() {
        return colorPalette.primaryTint();
    }

    @Override
    public String secondary() {
        return ansiSecondaryToneFg;
    }

    @Override
    public String complete() {
        return ansiPrimaryTintFg;
    }

    @Override
    public String completeBg() {
        return ansiPrimaryTintBg;
    }

    @Override
    public String incomplete() {
        return ansiPrimaryShadeFg;
    }

    @Override
    public String incompleteBg() {
        return ansiPrimaryShadeBg;
    }

    @Override
    public String start() {
        return ansiPrimaryShadeFg;
    }

    @Override
    public String priority() {
        return ansiAccentToneFg;
    }

    @Override
    public String addon() {
        return ansiSecondaryToneFg;
    }

    @Override
    public String postDue() {
        return ansiAccentToneFg;
    }

    @Override
    public String preDue() {
        return ansiPrimaryShadeFg;
    }

    @Override
    public String error() {
        return ansiAccentToneFg;
    }

    @Override
    public String info() {
        return ansiSecondaryToneFg;
    }

    @Override
    public String good() {
        return ansiPrimaryTintFg;
    }

    @Override
    public String warn() {
        return ansiAccentToneFg;
    }
}
