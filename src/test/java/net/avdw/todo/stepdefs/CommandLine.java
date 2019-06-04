package net.avdw.todo.stepdefs;

import cucumber.api.java8.En;
import net.avdw.todo.Main;
import org.pmw.tinylog.Logger;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import static junit.framework.TestCase.fail;

public class CommandLine implements En {
    public CommandLine() {
        PrintStream orig = System.err;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream test = new PrintStream(output);
        When("^I type the following arguments \"([^\"]*)\"$", (String command) -> {
            output.reset();
            System.setErr(test);
            Main.main(command.split("\\s"));
            System.setErr(orig);
        });
        Then("^I should get an error$", () -> {
            if (output.size() == 0) {
                fail();
            }
        });
        Then("^I should not get an error$", () -> {
            if (output.size() > 0) {
                Logger.error(output);
                fail();
            }
        });
    }
}
