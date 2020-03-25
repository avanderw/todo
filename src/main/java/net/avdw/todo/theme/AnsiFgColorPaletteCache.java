package net.avdw.todo.theme;

import com.google.inject.Inject;
import net.avdw.todo.color.ColorConverter;

public class AnsiFgColorPaletteCache implements ColorPalette<String> {
    private String primaryTint;
    private String primaryTone;
    private String primaryShade;
    private String secondaryTint;
    private String secondaryTone;
    private String secondaryShade;
    private String accentTint;
    private String accentTone;
    private String accentShade;

    @Inject
    AnsiFgColorPaletteCache(final ColorConverter colorConverter, final ColorPalette<Integer> colorPalette) {
        primaryTint = colorConverter.hexToAnsiFg(colorPalette.primaryTint());
        primaryTone = colorConverter.hexToAnsiFg(colorPalette.primaryTone());
        primaryShade = colorConverter.hexToAnsiFg(colorPalette.primaryShade());
        secondaryTint = colorConverter.hexToAnsiFg(colorPalette.secondaryTint());
        secondaryTone = colorConverter.hexToAnsiFg(colorPalette.secondaryTone());
        secondaryShade = colorConverter.hexToAnsiFg(colorPalette.secondaryShade());
        accentTint = colorConverter.hexToAnsiFg(colorPalette.accentTint());
        accentTone = colorConverter.hexToAnsiFg(colorPalette.accentTone());
        accentShade = colorConverter.hexToAnsiFg(colorPalette.accentShade());
    }

    @Override
    public String primaryTint() {
        return primaryTint;
    }

    @Override
    public String primaryTone() {
        return primaryTone;
    }

    @Override
    public String primaryShade() {
        return primaryShade;
    }

    @Override
    public String secondaryTint() {
        return secondaryTint;
    }

    @Override
    public String secondaryTone() {
        return secondaryTone;
    }

    @Override
    public String secondaryShade() {
        return secondaryShade;
    }

    @Override
    public String accentTint() {
        return accentTint;
    }

    @Override
    public String accentTone() {
        return accentTone;
    }

    @Override
    public String accentShade() {
        return accentShade;
    }
}
