package net.avdw.todo;

import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class CliTester {
    private final Class<?> cliClass;
    private final TestGuiceFactory guiceFactory;
    private StringWriter err;
    private StringWriter out;
    private int exitCode;

    public CliTester(final Class<?> cliClass, final TestGuiceFactory guiceFactory) {
        this.cliClass = cliClass;
        this.guiceFactory = guiceFactory;
    }

    public CliTester execute() {
        return execute(null);
    }

    public CliTester execute(final String command) {
        err = new StringWriter();
        out = new StringWriter();
        guiceFactory.reset();
        CommandLine commandLine = new CommandLine(cliClass, guiceFactory);
        commandLine.setOut(new PrintWriter(out));
        commandLine.setErr(new PrintWriter(err));
        if (command == null) {
            exitCode = commandLine.execute();
        } else {
            exitCode = commandLine.execute(command.split(" "));
        }
        return this;
    }

    public CliTester success() {
        assertSuccess(exitCode);
        return this;
    }

    private void assertSuccess(final int exitCode) {
        if (!out.toString().isEmpty()) {
            Logger.debug("Standard output:\n{}", out.toString());
        }
        if (!err.toString().isEmpty()) {
            Logger.error("Error output:\n{}", err.toString());
        }
        assertEquals("MUST NOT HAVE error output", "", err.toString().trim());
        assertNotEquals("MUST HAVE standard output", "", out.toString().trim());
        assertEquals(0, exitCode);
    }

    public CliTester failure() {
        assertFailure(exitCode);
        return this;
    }

    private void assertFailure(final int exitCode) {
        if (!out.toString().isEmpty()) {
            Logger.debug("Standard output:\n{}", out.toString());
        }
        if (!err.toString().isEmpty()) {
            Logger.debug("Error output:\n{}", err.toString());
        }
        assertNotEquals("MUST HAVE error output", "", err.toString().trim());
        assertEquals("MUST NOT HAVE standard output", "", out.toString().trim());
        assertNotEquals(0, exitCode);
    }

    public CliTester startsWith(final String text) {
        assertTrue(String.format("Output MUST start with '%s'", text), out.toString().startsWith(text));
        return this;
    }

    public CliTester contains(final String text) {
        assertTrue(String.format("Output MUST contain '%s'", text), out.toString().contains(text));
        return this;
    }

    public CliTester notContains(final String text) {
        assertFalse(String.format("Output MUST NOT contain '%s'", text), out.toString().contains(text));
        return this;
    }
}
