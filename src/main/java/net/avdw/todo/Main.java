package net.avdw.todo;

import picocli.CommandLine;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public final class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        CommandLine commandLine = new CommandLine(MainCli.class, new GuiceFactory(new MainModule()));
        commandLine.setCaseInsensitiveEnumValuesAllowed(true);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        PrintWriter err = new PrintWriter(new OutputStreamWriter(System.err, StandardCharsets.UTF_8), true);
        commandLine.setOut(out);
        commandLine.setErr(err);
        commandLine.execute(args);
        out.flush();
    }

}
