package net.avdw.todo.style;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.avdw.todo.PropertyFile;
import net.avdw.todo.color.ColorConverter;
import net.avdw.todo.style.painter.IPainter;
import org.fusesource.jansi.Ansi;

import java.util.List;
import java.util.Properties;

public class StyleModule extends AbstractModule {
    @Provides
    @Singleton
    @DefaultColor
    String defaultColor(final Properties properties) {
        return properties.getProperty("color.default") == null
                ? Ansi.ansi().reset().toString()
                : new ColorConverter().hexToAnsiFg(Integer.parseInt(properties.getProperty("color.default").replace("0x", ""), 16));
    }

    @Provides
    @Singleton
    @DefaultDoneColor
    String defaultDoneColor(final Properties properties, @DefaultColor final String defaultColor) {
        return properties.getProperty("color.done.default") == null
                ? defaultColor
                : new ColorConverter().hexToAnsiFg(Integer.parseInt(properties.getProperty("color.done.default").replace("0x", ""), 16));
    }

    @Provides
    @Singleton
    @DefaultParkedColor
    String defaultParkedColor(final Properties properties, @DefaultDoneColor final String defaultColor) {
        return properties.getProperty("color.parked.default") == null
                ? defaultColor
                : new ColorConverter().hexToAnsiFg(Integer.parseInt(properties.getProperty("color.parked.default").replace("0x", ""), 16));
    }

    @Provides
    @Singleton
    @DefaultRemovedColor
    String defaultRemovedColor(final Properties properties, @DefaultDoneColor final String defaultColor) {
        return properties.getProperty("color.removed.default") == null
                ? defaultColor
                : new ColorConverter().hexToAnsiFg(Integer.parseInt(properties.getProperty("color.removed.default").replace("0x", ""), 16));
    }

    @Provides
    @Singleton
    Properties properties() {
        PropertyFile propertyFile = new PropertyFile("net.avdw/todo");
        return propertyFile.read("style");
    }

    @Provides
    @Singleton
    List<IPainter> stylerList(final StylerBuilder stylerBuilder, final Properties properties) {
        return stylerBuilder.buildFrom(properties);
    }
}
