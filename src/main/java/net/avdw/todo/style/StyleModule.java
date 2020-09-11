package net.avdw.todo.style;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.avdw.todo.PropertyFile;
import net.avdw.todo.color.ColorConverter;
import org.fusesource.jansi.Ansi;

import java.util.List;
import java.util.Properties;

public class StyleModule extends AbstractModule {
    @Provides
    @Singleton
    String defaultColor(final Properties properties) {
        return properties.getProperty("color.default") == null
                ? Ansi.ansi().reset().toString()
                : new ColorConverter().hexToAnsiFg(Integer.parseInt(properties.getProperty("color.default").replace("0x", ""), 16));
    }

    @Provides
    @Singleton
    Properties properties() {
        PropertyFile propertyFile = new PropertyFile("net.avdw/todo");
        return propertyFile.read("style");
    }

    @Provides
    @Singleton
    StylerBuilder stylerBuilder(final String defaultColor) {
        return new StylerBuilder(defaultColor);
    }

    @Provides
    @Singleton
    List<IStyler> stylerList(final StylerBuilder stylerBuilder, final Properties properties) {
        return stylerBuilder.buildFrom(properties);
    }
}
