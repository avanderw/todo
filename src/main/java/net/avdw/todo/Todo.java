package net.avdw.todo;

import net.avdw.todo.cli.AddCli;
import net.avdw.todo.list.ListCli;
import picocli.CommandLine;

@CommandLine.Command(name = "todo", description = "A tool to manage todo lists.",
        subcommands = {AddCli.class, ListCli.class})
public class Todo implements Runnable {
    public void run() {
        CommandLine.usage(Todo.class, System.out);
    }
}
