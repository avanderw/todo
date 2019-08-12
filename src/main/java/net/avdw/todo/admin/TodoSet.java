package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.Console;
import net.avdw.todo.Property;
import net.avdw.todo.config.PropertyModule;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

@Command(name = "set", description = "Set a property")
public class TodoSet implements Runnable{

    @Option(names = "--auto-date-add", description = "Automatically add the date when creating a todo")
    private Boolean autoDateAdd;

    @Inject
    Properties properties;

    @Inject
    @Property
    Path propertyPath;

    @Override
    public void run() {
        if (autoDateAdd != null) {
            properties.setProperty(PropertyModule.AUTO_DATE_ADD, autoDateAdd.toString());
            Console.info(String.format("Setting %s=%s", PropertyModule.AUTO_DATE_ADD, autoDateAdd));
        }

        try {
            properties.store(new FileWriter(propertyPath.toFile()), "Todo Properties");
            Console.divide();
            Console.info("Property file saved");
        } catch (IOException e) {
            Console.error("Could not save property file");
        }
    }
}
