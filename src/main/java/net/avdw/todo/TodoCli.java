package net.avdw.todo;

import net.avdw.todo.admin.initialize.InitializerCli;
import net.avdw.todo.list.filtering.ListCli;
import picocli.CommandLine;

@CommandLine.Command(name = "todo", description = "A tool to admin todo lists.",
        subcommands = {CommandLine.HelpCommand.class, InitializerCli.class, ListCli.class})
public class TodoCli implements Runnable {
    public void run() {
        CommandLine.usage(TodoCli.class, System.out);
    }
}
