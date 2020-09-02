package net.avdw.todo;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class MainVersion implements CommandLine.IVersionProvider {
    @Override
    public String[] getVersion() throws Exception {
        if (getClass().getPackage().getImplementationVersion() == null) {
            String location = "target/maven-archiver/pom.properties";
            Path pomProperties = Paths.get(location);
            if (Files.exists(pomProperties)) {
                Logger.debug("Getting version from {}", pomProperties);
                try (FileReader fileReader = new FileReader(pomProperties.toFile(), StandardCharsets.UTF_8)) {
                    Properties properties = new Properties();
                    properties.load(fileReader);
                    return new String[]{properties.getProperty("version")};
                }
            }

            Path pomPath = Paths.get("pom.xml");
            if (Files.exists(pomPath)) {
                MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
                try (FileReader fileReader = new FileReader("pom.xml", StandardCharsets.UTF_8)) {
                    Model model = mavenXpp3Reader.read(fileReader);
                    return new String[]{model.getVersion()};
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
