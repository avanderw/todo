package net.avdw.todo.theme;

import com.google.inject.AbstractModule;

public class ThemeModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ColorTheme.class).to(DefaultColorTheme.class);
        bind(ColorPalette.class).to(SlateColorPalette.class);
        bind(Integer.class).annotatedWith(LineLength.class).toInstance(120);
        bind(Integer.class).annotatedWith(ProgressBarLength.class).toInstance(50);
    }
}
