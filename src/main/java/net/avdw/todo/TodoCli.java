package net.avdw.todo;

import net.avdw.todo.list.addition.AddCli;
import net.avdw.todo.list.completion.DoneCli;
import net.avdw.todo.list.filtering.ListCli;
import net.avdw.todo.list.prioritisation.PriorityCli;
import net.avdw.todo.list.removal.RemoveCli;
import net.avdw.todo.list.rewriting.ReplaceCli;
import net.avdw.todo.list.tracking.TrackCli;
import picocli.CommandLine;

@CommandLine.Command(name = "todo", description = "A tool to manage todo lists.",
        subcommands = {CommandLine.HelpCommand.class, AddCli.class, DoneCli.class, ListCli.class, PriorityCli.class, RemoveCli.class, ReplaceCli.class, TrackCli.class})
public class TodoCli implements Runnable {
    public void run() {
        CommandLine.usage(TodoCli.class, System.out);
    }
}
