package net.avdw.todo;

import cucumber.api.java8.En;

public class TrackingStepdefs implements En {
    public TrackingStepdefs() {
        When("^I property the list \"([^\"]*)\"$", (String name) -> {
            throw new UnsupportedOperationException();
        });
    }
}
