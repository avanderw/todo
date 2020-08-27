package net.avdw.todo;

import org.junit.Before;
import org.junit.Test;
import org.tinylog.Logger;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class MainCliTest {

    private CommandLine commandLine;
    private StringWriter errWriter;
    private StringWriter outWriter;

    private void assertSuccess(final int exitCode) {
        if (!outWriter.toString().isEmpty()) {
            Logger.debug("Standard output:\n{}", outWriter.toString());
        }
        if (!errWriter.toString().isEmpty()) {
            Logger.error("Error output:\n{}", errWriter.toString());
        }
        assertEquals("MUST NOT HAVE error output", "", errWriter.toString());
        assertNotEquals("MUST HAVE standard output", "", outWriter.toString().trim());
        assertEquals(0, exitCode);
    }

    @Before
    public void beforeTest() {
        commandLine = new CommandLine(MainCli.class, GuiceFactory.getInstance());
        errWriter = new StringWriter();
        outWriter = new StringWriter();
        commandLine.setOut(new PrintWriter(outWriter));
        commandLine.setErr(new PrintWriter(errWriter));
    }

    @Test
    public void testEmpty() {
        assertSuccess(commandLine.execute());
        assertTrue("SHOULD output usage help", outWriter.toString().contains("Usage"));
    }

    @Test
    public void testVersion() {
        assertSuccess(commandLine.execute("--version"));
        assertNotEquals(2, outWriter.toString().length());
    }
}