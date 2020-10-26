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
    Properties properties() {
        PropertyFile propertyFile = new PropertyFile("net.avdw/todo");
        return propertyFile.read("style");
    }
}
