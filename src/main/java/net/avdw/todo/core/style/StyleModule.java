package net.avdw.todo.core.style;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.avdw.property.PropertyFile;

import java.util.Properties;

public class StyleModule extends AbstractModule {
    @Provides
    @Singleton
    Properties properties() {
        PropertyFile propertyFile = new PropertyFile("net.avdw/todo");
        return propertyFile.read("style");
    }
}
