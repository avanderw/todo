package net.avdw.todo.wunderlist;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.pmw.tinylog.Logger;

import java.io.*;
import java.util.Properties;

public class WunderlistProperties {
    private final File file;

    @Inject
    WunderlistProperties(@Named("WUNDERLIST_PROPS") File file) {
        this.file = file;
    }

    Properties load() {
        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader(file)) {
            properties.load(fileReader);
        } catch (IOException e) {
            Logger.error(e);
        }

        return properties;
    }

    void save(Properties properties) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            properties.store(fileWriter, "Wunderlist properties");
        } catch (IOException e) {
            Logger.error(e);
        }
    }

}
