package net.avdw.todo;

import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

class PropertyFile {

    private final String name;
    private final File file;

    PropertyFile(String name) {
        this.name = name;
        this.file = new File(String.format("%s.properties", name));
    }

    Properties load() {
        Properties properties = new Properties();
        if (!file.exists()) {
            Logger.debug("Create property file");
            save(properties);
        }

        try (FileReader fileReader = new FileReader(file)) {
            properties.load(fileReader);
        } catch (IOException e) {
            Logger.error(e);
        }

        return properties;
    }

    private void save(Properties properties) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            properties.store(fileWriter, name);
        } catch (IOException e) {
            Logger.error(e);
        }
    }
}
