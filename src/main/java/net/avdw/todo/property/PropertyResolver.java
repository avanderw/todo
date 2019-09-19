package net.avdw.todo.property;

import com.google.inject.Inject;
import org.pmw.tinylog.Logger;

import java.util.Properties;

public class PropertyResolver {
    private final Properties environmentProperties;
    private final Properties systemProperties;
    private final Properties globalProperties;
    private final Properties localProperties;
    private final Properties defaultProperties;

    @Inject
    PropertyResolver(final @EnvironmentProperty Properties environmentProperties,
                     final @SystemProperty Properties systemProperties,
                     final @GlobalProperty Properties globalProperties,
                     final @LocalProperty Properties localProperties,
                     final @DefaultProperty Properties defaultProperties) {
        this.environmentProperties = environmentProperties;
        this.systemProperties = systemProperties;
        this.globalProperties = globalProperties;
        this.localProperties = localProperties;
        this.defaultProperties = defaultProperties;
    }

    /**
     * Run through the property hierarchy looking for overrides to properties.
     *
     * @param key the key of the property to search for
     * @return the first value found through the hierarchy
     */
    public String resolve(final String key) {
        Logger.debug(String.format("Resolving [%s]", key));
        Object value;
        if (environmentProperties.containsKey(key)) {
            Logger.debug("Environment override");
            value = environmentProperties.get(key);
        } else if (systemProperties.containsKey(key)) {
            Logger.debug("System override");
            value = systemProperties.get(key);
        } else if (globalProperties.containsKey(key)) {
            Logger.debug("Global override");
            value = globalProperties.get(key);
        } else if (localProperties.containsKey(key)) {
            Logger.debug("Local override");
            value = localProperties.get(key);
        } else if (defaultProperties.containsKey(key)) {
            Logger.debug("Default");
            value = defaultProperties.get(key);
        } else {
            throw new UnsupportedOperationException();
        }

        Logger.debug(String.format("Property %s=%s", key, value));
        return String.valueOf(value);
    }
}
