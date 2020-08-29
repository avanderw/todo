package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.property.GlobalProperty;
import net.avdw.todo.property.PropertyKey;
import net.avdw.todo.theme.ThemeApplicator;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

@Deprecated
@Command(name = "set", description = "Set a property")
public class TodoSet implements Runnable {

    @Option(names = "--auto-date-add", description = "Automatically add the date when creating a todo")
    private Boolean autoDateAdd;

    @Inject
    private Properties properties;

    @Inject
    @GlobalProperty
    private Path propertyPath;

    @Inject
    private ThemeApplicator themeApplicator;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        System.out.println(themeApplicator.header("todo:set"));
        if (autoDateAdd != null) {
            properties.setProperty(PropertyKey.TODO_ADD_AUTO_DATE, autoDateAdd.toString());
            Logger.info(String.format("Setting %s=%s", PropertyKey.TODO_ADD_AUTO_DATE, autoDateAdd));
        }

        try {
            properties.store(new FileWriter(propertyPath.toFile()), "Todo Properties");
            Logger.info("");
            Logger.info("Property file saved");
        } catch (IOException e) {
            Logger.warn("Could not save property file");
        }
    }
}
