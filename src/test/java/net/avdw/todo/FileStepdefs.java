package net.avdw.todo;

import cucumber.api.java8.En;
import org.pmw.tinylog.Logger;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;

public class FileStepdefs implements En {
    public FileStepdefs() {
        Given("^the file \"([^\"]*)\" does not exist$", (String path) -> {
            if (!new File(path).delete()) {
                Logger.debug(String.format("%s deleted", path));
            }
        });
        Then("^the file \"([^\"]*)\" exists$", (String path) -> assertThat(String.format("File %s must exist", path), new File(path).exists()));
    }
}
