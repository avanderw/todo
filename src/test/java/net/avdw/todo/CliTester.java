package net.avdw.todo;

import org.fusesource.jansi.AnsiConsole;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Utility class to assist with testing picocli applications.
 * <p>
 * The aim is to have one class that can be copied between projects.
 * The reason is that I hate dependency management on my own classes.
 * I have no problem with duplication, it makes code more modular.
 *
 * @version 2020-10-07: Added javadoc
 */
public class CliTester {
    private final Class<?> cliClass;
    private final TestGuiceFactory guiceFactory;
    private ByteArrayOutputStream err;
    private int exitCode;
    private ByteArrayOutputStream out;

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
        assertEquals(regex, count, matcher.results().count());
        return this;
    }

    public CliTester execute() {
        return execute(null);
    }

    public CliTester execute(final String command, final String... arguments) {
        err = new ByteArrayOutputStream();
        out = new ByteArrayOutputStream();
        guiceFactory.reset();
        CommandLine commandLine = new CommandLine(cliClass, guiceFactory);
        commandLine.setOut(new PrintWriter(AnsiConsole.wrapOutputStream(out), true, StandardCharsets.UTF_8));
        commandLine.setErr(new PrintWriter(AnsiConsole.wrapOutputStream(err), true, StandardCharsets.UTF_8));
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
