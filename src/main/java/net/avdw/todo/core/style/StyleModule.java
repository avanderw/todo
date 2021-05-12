package net.avdw.todo.core.style;

import dagger.Module;
import dagger.Provides;
import net.avdw.property.PropertyFile;

import javax.inject.Singleton;
import java.util.Properties;

@Module
public class StyleModule {
    @Provides
    @Singleton
    Properties properties() {
        final PropertyFile propertyFile = new PropertyFile("net.avdw/todo");
        return propertyFile.read("style");
    }
}
