package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.core.*;
import net.avdw.todo.plugin.browse.BrowseCli;
import net.avdw.todo.plugin.changelog.ChangelogCli;
import net.avdw.todo.plugin.edit.EditCli;
import net.avdw.todo.plugin.explore.ExploreCli;
import net.avdw.todo.plugin.link.LinkCli;
import net.avdw.todo.plugin.muscow.MoscowCli;
import net.avdw.todo.plugin.replace.ReplaceCli;
import net.avdw.todo.plugin.start.StartCli;
import net.avdw.todo.plugin.stats.StatsCli;
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
                StatsCli.class,
                ArchiveCli.class,
                BackupCli.class,
                ExploreCli.class,
                BrowseCli.class,
                StartCli.class,
                ReplaceCli.class,
                LinkCli.class,
                MoscowCli.class
        })
public class MainCli implements Runnable {
    @Spec
    private CommandSpec spec;
    @Inject
    private Path todoPath;

    @Override
    public void run() {
        if (Files.exists(todoPath)) {
            spec.commandLine().usage(spec.commandLine().getOut());
        } else {
            spec.commandLine().getOut().println(spec.commandLine().getSubcommands().get("init").getUsageMessage());
        }
    }
}
