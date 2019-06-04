package net.avdw.todo;

import cucumber.api.java8.En;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PropertyStepdefs implements En {
    private Properties properties;

    public PropertyStepdefs() {
        Given("^property \"([^\"]*)\" is not set$", (String property) -> {
            assertThat(properties.getProperty(property), is(nullValue()));
        });
        When("^I load properties from \"([^\"]*)\"", (String name) -> {
            throw new UnsupportedOperationException();
        });
        Then("^the \"([^\"]*)\" property \"([^\"]*)\" will be set to \"([^\"]*)\"$", (String context, String property, String value) -> {
            assertThat(properties.getProperty(property), is(equalTo(value)));
        });

    }
}
