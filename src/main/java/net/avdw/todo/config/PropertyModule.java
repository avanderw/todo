package net.avdw.todo.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.avdw.todo.Console;
import net.avdw.todo.Global;
import net.avdw.todo.Property;
import net.avdw.todo.action.TodoAdd;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class PropertyModule extends AbstractModule {
    public static final String TODO_PATHS = "todo.paths";
    public static final String AUTO_DATE_ADD = String.format("%s.date", TodoAdd.class.getCanonicalName());

    private static final String PROPERTY_FILE = "todo.properties";

    @Provides
    @Singleton
    @Property
    Path propertyPath(final @Global Path globalPath) {
        return globalPath.resolve(PROPERTY_FILE);
    }

    @Provides
    @Singleton
    Properties properties(final @Property Path propertyPath) {
        Properties properties = new Properties();
        if (Files.exists(propertyPath)) {
            try {
                properties.load(new FileReader(propertyPath.toFile()));
            } catch (IOException e) {
                Console.error("Could not load property file");
            }
        } else {
            try {
                properties.store(new FileWriter(propertyPath.toFile()), "Todo Properties");
            } catch (IOException e) {
                Console.error("Could not save property file");
            }
        }
        return properties;
    }
}
