package net.avdw.todo;

import org.fusesource.jansi.AnsiConsole;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
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
 * @version 2021-04-19 Fixed command output in success & failure to always show
 * 2020-10-07: Added javadoc
 */
public class CliTester {
    private final Path todoPath;
    private ByteArrayOutputStream err;
    private int exitCode;
    private ByteArrayOutputStream out;
    private String[] lastArgs;

    public CliTester(Path todoPath) {
        this.todoPath = todoPath;
    }

    public CliTester contains(final String text) {
        assertTrue(String.format("Output MUST contain '%s'", text), out.toString().contains(text));
        return this;
    }

    public CliTester count(final String regex, final int count) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(out.toString());
        assertEquals(String.format("Counting regex '%s' mismatch", regex), count, matcher.results().count());
        return this;
    }

    public CliTester execute() {
        return execute(null);
    }

    public CliTester execute(final String command) {
        return execute(command, null);
    }

    public CliTester execute(final String command, final String... arguments) {
        TestMainModule module = new TestMainModule(todoPath);
        TestMainComponent component = DaggerTestMainComponent.builder()
                .testMainModule(module)
                .build();
        CommandLine commandLine = new CommandLine(MainCli.class, new DaggerFactory(component));

        commandLine.setCaseInsensitiveEnumValuesAllowed(true);
        commandLine.setOut(new PrintWriter(AnsiConsole.wrapOutputStream(out = new ByteArrayOutputStream()), true));
        commandLine.setErr(new PrintWriter(AnsiConsole.wrapOutputStream(err = new ByteArrayOutputStream()), true, StandardCharsets.UTF_8));

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
            lastArgs = args;
            exitCode = commandLine.execute(args);
        }
        return this;
    }

    public CliTester failure() {
        Logger.debug("COMMAND: {}", Arrays.toString(lastArgs));
        if (!out.toString().isEmpty()) {
            Logger.debug("OUTPUT: \n{}", out.toString());
        }
        if (!err.toString().isEmpty()) {
            Logger.error("ERROR: \n{}", err.toString());
        }
        assertFailure(exitCode);
        return this;
    }

    private void assertFailure(final int exitCode) {
        assertNotEquals("MUST HAVE error output", "", err.toString().trim());
        assertEquals("MUST NOT HAVE standard output", "", out.toString().trim());
        assertNotEquals(0, exitCode);
    }

    public CliTester notContains(final String text) {
        assertFalse(String.format("Output MUST NOT contain '%s'", text), out.toString().contains(text));
        assertFalse(String.format("Error MUST NOT contain '%s'", text), err.toString().contains(text));
        return this;
    }

    public CliTester startsWith(final String text) {
        assertTrue(String.format("Output MUST start with '%s'", text), out.toString().startsWith(text));
        return this;
    }

    public CliTester success() {
        Logger.debug("COMMAND: {}", Arrays.toString(lastArgs));
        if (!out.toString().isEmpty()) {
            Logger.debug("OUTPUT: \n{}", out.toString());
        }
        if (!err.toString().isEmpty()) {
            Logger.error("ERROR: \n{}", err.toString());
        }
        assertSuccess(exitCode);
        return this;
    }

    private void assertSuccess(final int exitCode) {
        assertEquals("MUST NOT HAVE error output", "", err.toString().trim());
        assertNotEquals("MUST HAVE standard output", "", out.toString().trim());
        assertEquals(0, exitCode);
    }
}
