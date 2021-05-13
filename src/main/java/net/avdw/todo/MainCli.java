package net.avdw.todo;

import net.avdw.todo.core.AddCli;
import net.avdw.todo.core.ArchiveCli;
import net.avdw.todo.core.BackupCli;
import net.avdw.todo.core.DoneCli;
import net.avdw.todo.core.InitCli;
import net.avdw.todo.core.ListCli;
import net.avdw.todo.core.ParkCli;
import net.avdw.todo.core.PriorityCli;
import net.avdw.todo.core.RemoveCli;
import net.avdw.todo.core.SortCli;
import net.avdw.todo.core.StatusCli;
import net.avdw.todo.extension.browse.BrowseCli;
import net.avdw.todo.extension.changelog.ChangelogCli;
import net.avdw.todo.extension.comment.CommentCli;
import net.avdw.todo.extension.dependency.DependencyCli;
import net.avdw.todo.extension.edit.EditCli;
import net.avdw.todo.extension.moscow.MoscowCli;
import net.avdw.todo.extension.plan.PlanCli;
import net.avdw.todo.extension.replace.ReplaceCli;
import net.avdw.todo.extension.size.SizeCli;
import net.avdw.todo.extension.start.StartCli;
import net.avdw.todo.extension.stats.StatsCli;
import net.avdw.update.adapter.in.UpdateCliAdapter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
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
                PlanCli.class,
                CommentCli.class,
                UpdateCliAdapter.class
        })
public class MainCli implements Runnable {
    private final Path todoPath;
    @Spec
    private CommandSpec spec;

    @Inject
    MainCli(final Path todoPath) {
        this.todoPath = todoPath;
    }

    @Override
    public void run() {
        if (Files.exists(todoPath)) {
            spec.commandLine().usage(spec.commandLine().getOut());
        } else {
            spec.commandLine().getOut().println(spec.commandLine().getSubcommands().get("init").getUsageMessage());
        }
    }
}
