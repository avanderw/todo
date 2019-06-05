package net.avdw.todo;

import cucumber.api.java8.En;
import net.avdw.todo.Main;
import org.pmw.tinylog.Logger;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLOutput;

import static junit.framework.TestCase.fail;

public class TodoCliStepdefs implements En {
    public TodoCliStepdefs() {
        final PrintStream originalErrStream = System.err;
        final ByteArrayOutputStream errOutput = new ByteArrayOutputStream();
        final PrintStream testErrStream = new PrintStream(errOutput);
        
        When("^I type the arguments \"([^\"]*)\"$", (String command) -> {
            errOutput.reset();
            System.setErr(testErrStream);
            Main.main(command.split("\\s"));
            System.out.println();
            System.setErr(originalErrStream);
        });
        Then("^I should get an error$", () -> {
            if (errOutput.size() == 0) {
                fail();
            }
        });
        Then("^I should not get an error$", () -> {
            if (errOutput.size() > 0) {
                System.err.println(errOutput);
                fail();
            }
        });
    }
}
