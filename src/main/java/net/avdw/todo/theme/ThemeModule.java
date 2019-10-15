package net.avdw.todo.theme;

import com.google.inject.AbstractModule;

public class ThemeModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ColorTheme.class).to(DefaultColorTheme.class);
        bind(ColorPalette.class).to(GrayscaleColorPalette.class);
        bind(Integer.class).annotatedWith(LineLength.class).toInstance(80);
    }
}
