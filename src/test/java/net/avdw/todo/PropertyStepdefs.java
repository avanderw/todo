package net.avdw.todo;

import com.google.inject.Guice;
import cucumber.api.java8.En;
import net.avdw.todo.property.PropertyModule;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;

public class PropertyStepdefs implements En {
    private final Path propertyDir = Paths.get("target/test/properties");
    private final Path propertyPath = propertyDir.resolve(".todo.properties");

    public PropertyStepdefs() {
        Given("^there is no properties file$", () -> {
            if (Files.exists(propertyPath)) {
                Files.delete(propertyPath);
            }
        });
        When("^the injector is configured$", () -> Guice.createInjector(new PropertyModule(propertyDir)));
        Then("^the properties file is created$", () -> assertThat("Property file exists", Files.exists(propertyPath)));
    }
}
