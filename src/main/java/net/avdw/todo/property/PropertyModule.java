package net.avdw.todo.property;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.avdw.todo.Global;
import net.avdw.todo.LocalTodo;
import org.pmw.tinylog.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class PropertyModule extends AbstractModule {
    public static final String TODO_PATHS = "todo.paths";

    private static final String PROPERTY_FILE = "todo.properties";

    @Provides
    @Singleton
    @GlobalProperty
    Path globalPropertiesPath(final @Global Path globalTodoPath) {
        Logger.debug(String.format("Global properties path: %s", globalTodoPath.resolve(PROPERTY_FILE)));
        return globalTodoPath.resolve(PROPERTY_FILE);
    }

    @Provides
    @Singleton
    @LocalProperty
    Path localPropertiesPath(final @LocalTodo Path localTodoPath) {
        Logger.debug(String.format("Local properties path: %s", localTodoPath.resolve(PROPERTY_FILE)));
        return localTodoPath.resolve(PROPERTY_FILE);
    }

    @Provides
    @Singleton
    @EnvironmentProperty
    Properties environmentProperties() {
        Properties properties = new Properties();
        properties.putAll(java.lang.System.getenv());
        return properties;
    }

    @Provides
    @Singleton
    @SystemProperty
    Properties systemProperties() {
        return java.lang.System.getProperties();
    }

    @Provides
    @Singleton
    @GlobalProperty
    Properties globalProperties(final @GlobalProperty Path globalPropertyPath) {
        return getProperties(globalPropertyPath);
    }

    @Provides
    @Singleton
    @LocalProperty
    Properties localProperties(final @LocalProperty Path localPropertyPath) {
        return getProperties(localPropertyPath);
    }

    @Provides
    @Singleton
    @DefaultProperty
    Properties defaultProperties() {
        Properties properties = new Properties();
        properties.put(PropertyKey.TODO_ADD_AUTO_DATE, "false");
        properties.put(PropertyKey.RELEASE_MODE, "true");
        properties.put(PropertyKey.LOGGING_LEVEL, "INFO");
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            properties.put(PropertyKey.EDITOR_PATH, "notepad.exe");
        } else {
            Logger.error(String.format("Unknown OS: %s", System.getProperty("os.name")));
            throw new UnsupportedOperationException();
        }
        return properties;
    }

    private Properties getProperties(final Path propertyPath) {
        Properties properties = new Properties();
        if (Files.exists(propertyPath)) {
            try {
                properties.load(new FileReader(propertyPath.toFile()));
                Logger.debug(String.format("Loaded property file: %s", propertyPath));
            } catch (IOException e) {
                Logger.error("Could not load property file");
            }
        } else {
            try {
                if (Files.exists(propertyPath.getParent())) {
                    properties.store(new FileWriter(propertyPath.toFile()), "Todo Properties");
                    Logger.debug(String.format("Wrote property file: %s", propertyPath));
                }
            } catch (IOException e) {
                Logger.error("Could not save property file");
            }
        }
        return properties;
    }
}
