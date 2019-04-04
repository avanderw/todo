package net.avdw.todo;

import net.avdw.todo.add.AddCli;
import net.avdw.todo.done.DoneCli;
import net.avdw.todo.list.ListCli;
import net.avdw.todo.remove.RemoveCli;
import net.avdw.todo.replace.ReplaceCli;
import picocli.CommandLine;

@CommandLine.Command(name = "todo", description = "A tool to manage todo lists.",
        subcommands = {AddCli.class, DoneCli.class, ListCli.class, RemoveCli.class, ReplaceCli.class})
public class Todo implements Runnable {
    public void run() {
        CommandLine.usage(Todo.class, System.out);
    }
}
