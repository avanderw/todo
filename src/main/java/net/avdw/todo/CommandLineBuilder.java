package net.avdw.todo;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import org.reflections.Reflections;
import picocli.CommandLine;

import java.io.PrintWriter;

public class CommandLineBuilder {
    private final PrintWriter out;
    private final PrintWriter err;

    @Inject
    CommandLineBuilder(@Named("out") final PrintWriter out, @Named("err") final PrintWriter err) {
        this.out = out;
        this.err = err;
    }

    public CommandLine build(final Class<?> cliClass, final Injector injector) {
        CommandLine commandLine = new CommandLine(cliClass, new GuiceFactory(injector));

        Reflections reflections = new Reflections("net.avdw.todo.action");
        reflections.getTypesAnnotatedWith(CommandLine.Command.class).forEach(commandLine::addSubcommand);

        commandLine.setCaseInsensitiveEnumValuesAllowed(true);
        commandLine.setOut(out);
        commandLine.setErr(err);
        return commandLine;
    }
}
