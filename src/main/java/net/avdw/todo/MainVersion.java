package net.avdw.todo;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
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
     * - pom.xml        : When the target directory does not exist
     *
     * @return The version specified in the pom
     */
    @Override
    public String[] getVersion() {
        if (getClass().getPackage().getImplementationVersion() == null) {
            String location = "target/maven-archiver/pom.properties";
            Path pomProperties = Paths.get(location);
            if (Files.exists(pomProperties)) {
                Logger.debug("Getting version from {}", pomProperties);
                try (FileReader fileReader = new FileReader(pomProperties.toFile(), StandardCharsets.UTF_8)) {
                    Properties properties = new Properties();
                    properties.load(fileReader);
                    Logger.debug("Getting version from pom.properties");
                    return new String[]{properties.getProperty("version")};
                } catch (IOException e) {
                    Logger.debug(e);
                }
            }

            Path pomPath = Paths.get("pom.xml");
            if (Files.exists(pomPath)) {
                MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
                try (FileReader fileReader = new FileReader("pom.xml", StandardCharsets.UTF_8)) {
                    Model model = mavenXpp3Reader.read(fileReader);
                    Logger.debug("Getting version from pom.xml");
                    return new String[]{model.getVersion()};
                } catch (XmlPullParserException | IOException e) {
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
            throw new UnsupportedOperationException();
        } else {
            Logger.debug("Getting version from manifest");
            return new String[]{getClass().getPackage().getImplementationVersion()};
        }
    }
}
