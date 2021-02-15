package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.core.*;
import net.avdw.todo.extension.browse.BrowseCli;
import net.avdw.todo.extension.changelog.ChangelogCli;
import net.avdw.todo.extension.edit.EditCli;
import net.avdw.todo.extension.dependency.DependencyCli;
import net.avdw.todo.extension.moscow.MoscowCli;
import net.avdw.todo.extension.plan.PlanCli;
import net.avdw.todo.extension.replace.ReplaceCli;
import net.avdw.todo.extension.size.SizeCli;
import net.avdw.todo.extension.start.StartCli;
import net.avdw.todo.extension.stats.StatsCli;
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
                StatusCli.class,
                StatsCli.class,
                ArchiveCli.class,
                BackupCli.class,
                BrowseCli.class,
                StartCli.class,
                ReplaceCli.class,
                DependencyCli.class,
                MoscowCli.class,
                SizeCli.class,
                PlanCli.class
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
