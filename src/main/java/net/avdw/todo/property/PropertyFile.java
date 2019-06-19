package net.avdw.todo.property;

import net.avdw.todo.list.tracking.TrackedList;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

class PropertyFile implements APropertyRepository {

    private final File file;
    private Properties cache;

    PropertyFile(Path propertyDir) {
        this.file = propertyDir.resolve(".todo.properties").toFile();
    }

    @Override
    public Properties getProperties() {
        if (cache == null) {
            cache = loadProperties();
        }

        return cache;
    }

    @Override
    public String getProperty(String key) {
        return getProperties().getProperty(key);
    }

    @Override
    public void saveProperty(String key, String value) {
        Properties properties = getProperties();
        properties.setProperty(key, value);
        saveProperties(properties);
        Logger.debug(String.format("Refreshing cache %s", cache));
        cache = loadProperties();
    }

    private Properties createProperties() {
        Properties properties = new Properties();
        properties.setProperty(TrackedList.name, "");
        return properties;
    }

    private void saveProperties(Properties properties) {
        try {
            if (!file.exists() && file.getParentFile().mkdirs() && file.createNewFile()) {
                Logger.debug(String.format("%s created", file));
            }
        } catch (IOException e) {
            Logger.error(e);
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            properties.store(fileWriter, "Todo by @avanderw properties");
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    private Properties loadProperties() {
        if (!file.exists()) {
            Logger.debug("Creating properties");
            Properties properties = createProperties();
            saveProperties(properties);
        }

        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader(file)) {
            properties.load(fileReader);
            Logger.debug(String.format("Loaded properties %s", properties));
        } catch (IOException e) {
            Logger.error(e);
        }

        return properties;
    }
}
