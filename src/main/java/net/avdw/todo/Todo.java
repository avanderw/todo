package net.avdw.todo;

import net.avdw.todo.add.AddCli;
import net.avdw.todo.done.DoneCli;
import net.avdw.todo.list.ListCli;
import net.avdw.todo.priority.PriorityCli;
import net.avdw.todo.remove.RemoveCli;
import net.avdw.todo.replace.ReplaceCli;
import net.avdw.todo.tracking.TrackCli;
import picocli.CommandLine;

@CommandLine.Command(name = "todo", description = "A tool to manage todo lists.",
        subcommands = {CommandLine.HelpCommand.class, AddCli.class, DoneCli.class, ListCli.class, PriorityCli.class, RemoveCli.class, ReplaceCli.class, TrackCli.class})
public class Todo implements Runnable {
    public void run() {
        CommandLine.usage(Todo.class, System.out);
    }
}
