package net.avdw.todo.theme;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

public class ThemeModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ColorTheme.class).to(DefaultColorTheme.class);
        bind(Theme.class).to(DefaultTheme.class);

        bind(new TypeLiteral<ColorPalette<Integer>>() {
        }).to(GrayscaleColorPalette.class);
        bind(new TypeLiteral<ColorPalette<String>>() {
        }).to(AnsiFgColorPaletteCache.class);

        bind(Integer.class).annotatedWith(LineLength.class).toInstance(120);
        bind(Integer.class).annotatedWith(ProgressBarLength.class).toInstance(50);
    }
}
