package net.avdw.todo;

import com.google.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.nio.file.Files;
import java.nio.file.Path;

@Command(name = "todo",
        resourceBundle = "messages",
        description = "${bundle:main}",
        versionProvider = MainVersion.class,
        mixinStandardHelpOptions = true,
        subcommands = {
                InitCli.class,
                AddCli.class,
                ListCli.class,
                PriorityCli.class,
                DoneCli.class,
                ParkCli.class,
                RemoveCli.class,
                SortCli.class,
                EditCli.class,
                ChangelogCli.class,
                ArchiveCli.class,
                BackupCli.class,
                ExploreCli.class,
        })
public class MainCli implements Runnable {
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
