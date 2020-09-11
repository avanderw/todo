package net.avdw.todo;

import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class CliTester {
    private final Class<?> cliClass;
    private final TestGuiceFactory guiceFactory;
    private StringWriter err;
    private int exitCode;
    private StringWriter out;

    public CliTester(final Class<?> cliClass, final TestGuiceFactory guiceFactory) {
        this.cliClass = cliClass;
        this.guiceFactory = guiceFactory;
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

    public CliTester contains(final String text) {
        assertTrue(String.format("Output MUST contain '%s'", text), out.toString().contains(text));
        return this;
    }

    public CliTester count(final String regex, final int count) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(out.toString());
        assertEquals(count, matcher.results().count());
        return this;
    }

    public CliTester execute() {
        return execute(null);
    }

    public CliTester execute(final String command, final String ...arguments) {
        err = new StringWriter();
        out = new StringWriter();
        guiceFactory.reset();
        CommandLine commandLine = new CommandLine(cliClass, guiceFactory);
        commandLine.setOut(new PrintWriter(out));
        commandLine.setErr(new PrintWriter(err));
        if (command == null) {
            exitCode = commandLine.execute();
        } else {
            String[] splitCommand = command.split(" ");
            String[] args;
            if (arguments == null) {
                args = splitCommand;
            } else {
                args = new String[splitCommand.length + arguments.length];
                System.arraycopy(splitCommand, 0, args, 0, splitCommand.length);
                System.arraycopy(arguments, 0, args, splitCommand.length, arguments.length);
            }
            exitCode = commandLine.execute(args);
        }
        return this;
    }

    public CliTester execute(final String command) {
        return execute(command, null);
    }

    public CliTester failure() {
        assertFailure(exitCode);
        return this;
    }

    public CliTester notContains(final String text) {
        assertFalse(String.format("Output MUST NOT contain '%s'", text), out.toString().contains(text));
        assertFalse(String.format("Error MUST NOT contain '%s'", text), err.toString().contains(text));
        return this;
    }

    public CliTester notStartsWith(final String text) {
        assertFalse(String.format("Output MUST NOT start with '%s'", text), out.toString().startsWith(text));
        assertFalse(String.format("Error MUST NOT start with '%s'", text), err.toString().startsWith(text));
        return this;
    }

    public CliTester startsWith(final String text) {
        assertTrue(String.format("Output MUST start with '%s'", text), out.toString().startsWith(text));
        return this;
    }

    public CliTester success() {
        assertSuccess(exitCode);
        return this;
    }
}
