package net.avdw.todo.config;

import net.avdw.todo.list.tracking.TrackedList;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

class PropertyFile implements APropertyRepository {

    private final String name;
    private final File file;
    private Properties cache;

    PropertyFile(String name) {
        this.name = name;
        this.file = new File(String.format("%s.properties", name));
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
        try (FileWriter fileWriter = new FileWriter(file)) {
            properties.store(fileWriter, String.format("%s config plaintext", name));
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    private Properties loadProperties() {
        if (!file.exists()) {
            Logger.debug("Creating config plaintext");
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
