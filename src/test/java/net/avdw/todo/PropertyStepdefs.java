package net.avdw.todo;

import cucumber.api.java8.En;

import java.util.Properties;

public class PropertyStepdefs implements En {
    private Properties properties;

    public PropertyStepdefs() {
        When("^I load properties from \"([^\"]*)\" properties$", (String name) -> {
            PropertyFile propertyFile = new PropertyFile(name);
            properties = propertyFile.load();
        });
    }
}
