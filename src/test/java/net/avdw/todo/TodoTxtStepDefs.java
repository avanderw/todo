package net.avdw.todo;

import cucumber.api.java8.En;
import net.avdw.todo.cli.AddFunc;
import net.avdw.todo.list.ListFunc;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TodoTxtStepDefs implements En {
    private File file;
    private List<String> items;

    public TodoTxtStepDefs() {
        Given("^I track the file \"([^\"]*)\"$", (String name) -> {
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
        Given("^the file \"([^\"]*)\" does not exist$", (String path) -> {
            if (!new File(path).delete()) {
                Logger.debug(String.format("%s removed", path));
            }
        });
        Given("^I copy the file \"([^\"]*)\" to \"([^\"]*)\"$", (String from, String to) -> {
            Files.copy(Paths.get(from), Paths.get(to));
        });
        When("^I add a todo item$", () -> {
            new AddFunc(file).add(UUID.randomUUID().toString());
        });
        And("^the last item will have a created date of now$", () -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            assertThat("Must start with today's date", items.get(items.size()-1).startsWith(sdf.format(new Date()), 5));
        });
    }
}
