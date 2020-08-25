package net.avdw.todo;

import lombok.SneakyThrows;
import org.tinylog.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * A single class property file loader.
 * <p>
 * The aim is to have one class that can be copied between projects.
 * The reason is that I hate dependency management on my own classes.
 * I have no problem with duplication, it makes code more modular.
 *
 * @version 2020-08-24: One class property loader
 */
public class PropertyFile {
    private final String namespace;

    @Inject
    public PropertyFile(@Named("namespace") final String namespace) {
        this.namespace = namespace;
    }

    /**
     * Read all property files adhering to the following load order:
     * - jar    : search for the property file on the classpath
     * - local  : search for property file in the application directory
     * - global : search for property file from a system directory
     * Thus global properties overwrite local properties which overwrite jar properties.
     *
     * @param name the name of the property file
     * @return Properties adhering to the load order
     */
    @SneakyThrows
    public Properties read(final String name) {
        String propertyFilename = String.format("%s.properties", name);
        Properties properties = new Properties();

        String classPath = String.format("/%s", propertyFilename);
        if (getClass().getResourceAsStream(classPath) != null) {
            Properties prop = new Properties();
            prop.load(getClass().getResourceAsStream(classPath));
            properties.putAll(prop);
            int width = prop.keySet().stream().mapToInt(k -> k.toString().length()).max().orElseThrow();
            Logger.debug("Load class properties ({}):" +
                    "\n{}", classPath, prop.entrySet().stream()
                    .map(e -> String.format("%-"+width+"s = %s", e.getKey(), e.getValue()))
                    .sorted()
                    .collect(Collectors.joining("\n")));
        }

        Path localPath = Paths.get(propertyFilename);
        if (Files.exists(localPath)) {
            Properties prop = new Properties();
            prop.load(new FileReader(localPath.toString(), StandardCharsets.UTF_8));
            properties.putAll(prop);
            Logger.debug("Load local properties ({}):" +
                    "\n{}", localPath, prop);
        }

        Path globalPath = Paths.get(System.getProperty("user.home")).resolve(namespace).resolve(propertyFilename);
        if (Files.exists(globalPath)) {
            Properties prop = new Properties();
            prop.load(new FileReader(globalPath.toString(), StandardCharsets.UTF_8));
            properties.putAll(prop);
            Logger.debug("Load local properties ({}):" +
                    "\n{}", globalPath, prop);
        }
        return properties;
    }
}
