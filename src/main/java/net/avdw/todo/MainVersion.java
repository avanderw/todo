package net.avdw.todo;

import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Version provider for picocli which calculates the version from build files.
 * <p>
 * The aim is to have one class that can be copied between projects.
 * The reason is that I hate dependency management on my own classes.
 * I have no problem with duplication, it makes code more modular.
 *
 * @version 2020-10-07: Added javadoc
 */
public class MainVersion implements CommandLine.IVersionProvider {
    /**
     * Return as soon as a version is found. The order of the lookup is:
     * - manifest       : When built as a jar
     * - pom.properties : When target directory exists
     *
     * @return The version specified in the pom
     */
    @Override
    public String[] getVersion() {
        if (getClass().getPackage().getImplementationVersion() == null) {
            final String location = "target/maven-archiver/pom.properties";
            final Path pomProperties = Paths.get(location);
            if (Files.exists(pomProperties)) {
                Logger.debug("Getting version from {}", pomProperties);
                try (FileReader fileReader = new FileReader(pomProperties.toFile(), StandardCharsets.UTF_8)) {
                    final Properties properties = new Properties();
                    properties.load(fileReader);
                    Logger.debug("Getting version from pom.properties");
                    return new String[]{properties.getProperty("version")};
                } catch (final IOException e) {
                    Logger.debug(e);
                }
            }

            Logger.error("Implementation version not found. This class is intended for use with Maven:\n" +
                    "<archive>\n" +
                    "    <manifest>\n" +
                    "        <addDefaultImplementationEntries>true</addDefaultImplementationEntries>\n" +
                    "        <addDefaultSpecificationEntries>true</addDefaultSpecificationEntries>\n" +
                    "    </manifest>\n" +
                    "</archive>");
            return new String[]{"no-version"};
        } else {
            Logger.debug("Getting version from manifest");
            return new String[]{getClass().getPackage().getImplementationVersion()};
        }
    }
}
