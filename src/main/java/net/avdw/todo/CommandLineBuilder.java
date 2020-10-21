package net.avdw.todo;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import picocli.CommandLine;

import java.io.PrintWriter;

public class CommandLineBuilder {
    private final PrintWriter err;
    private final PrintWriter out;

    @Inject
    CommandLineBuilder(@Named("out") final PrintWriter out, @Named("err") final PrintWriter err) {
        this.out = out;
        this.err = err;
    }

    public CommandLine build(final Class<?> cliClass, final Injector injector) {
        CommandLine commandLine = new CommandLine(cliClass, new GuiceFactory(injector));

        commandLine.setCaseInsensitiveEnumValuesAllowed(true);
        commandLine.setOut(out);
        commandLine.setErr(err);
        return commandLine;
    }
}
