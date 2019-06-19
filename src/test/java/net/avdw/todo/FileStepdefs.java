package net.avdw.todo;

import cucumber.api.java8.En;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.hamcrest.MatcherAssert.assertThat;

public class FileStepdefs implements En {
    public FileStepdefs() {
        Given("^I copy the plaintext \"([^\"]*)\" to \"([^\"]*)\"$", (String from, String to) ->
                Files.copy(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING));
        Given("^I delete the plaintext \"([^\"]*)\"$", (String path) -> {
            if (!new File(path).delete()) {
                Logger.debug(String.format("%s deleted", path));
            }
        });
        Then("^the plaintext \"([^\"]*)\" exists$", (String path) -> assertThat(String.format("PlainText %s must exist", path), new File(path).exists()));
    }
}
