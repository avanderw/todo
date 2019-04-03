package net.avdw.todo;

import cucumber.api.java8.En;
import net.avdw.todo.list.ListFunc;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TodoTxtStepDefs implements En {
    private File file;
    private Set<String> items;

    public TodoTxtStepDefs() {
        Given("^the file \"([^\"]*)\"$", (String name) -> {
            file = new File(name);
        });
        When("^I list the todo items with no arguments$", () -> {
            items = new ListFunc(file).list();
        });
        Then("^I will get a list with (\\d+) items$", (Integer num) -> {
            assertThat(items.size(), is(equalTo(num)));
        });
        When("^I list the todo items with arguments \"([^\"]*)\"$", (String args) -> {
            items = new ListFunc(file).list(Arrays.asList(args.split(" ")));
        });
    }
}
