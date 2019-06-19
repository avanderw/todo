package net.avdw.todo.list;

import cucumber.api.java8.En;

public class FilteringStepdefs implements En {
    public FilteringStepdefs() {
        Given("^the task repository \"([^\"]*)\"$", (String repository) -> {

            throw new UnsupportedOperationException();
        });
        When("^I list the todo items$", () -> {
            throw new UnsupportedOperationException();
        });
        Then("^the list will contain (\\d+) items$", (Integer num) -> {
            throw new UnsupportedOperationException();
        });
        When("^I list the todo items with filter \"([^\"]*)\"$", (String filter) -> {
            throw new UnsupportedOperationException();
        });
        When("^I list the contexts$", () -> {
            throw new UnsupportedOperationException();
        });
        When("^I list the projects$", () -> {
            throw new UnsupportedOperationException();
        });
    }
}
