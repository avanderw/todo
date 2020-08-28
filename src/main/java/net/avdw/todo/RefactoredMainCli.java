package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.admin.TodoInit;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Command(name = "todo",
        resourceBundle = "messages",
        description = "${bundle:main}",
        versionProvider = MainVersion.class,
        mixinStandardHelpOptions = true,
        subcommands = {
                InitCli.class,
                ParkCli.class,
                RemoveCli.class,
        })
public class RefactoredMainCli implements Runnable {
    @Inject
    private Path todoPath;

    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        if (Files.exists(todoPath)) {
            spec.commandLine().usage(spec.commandLine().getOut());
        } else {
            spec.commandLine().getOut().println(spec.commandLine().getSubcommands().get("init").getUsageMessage());
        }
    }
}
