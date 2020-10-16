package net.avdw.todo;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import net.avdw.todo.plugin.Plugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import picocli.CommandLine;
import picocli.CommandLine.Command;

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

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("net.avdw.todo.plugin"))
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner()));
        reflections.getTypesAnnotatedWith(Command.class).stream()
                .filter(c -> c.isAnnotationPresent(Plugin.class))
                .forEach(commandLine::addSubcommand);

        commandLine.setCaseInsensitiveEnumValuesAllowed(true);
        commandLine.setOut(out);
        commandLine.setErr(err);
        return commandLine;
    }
}
