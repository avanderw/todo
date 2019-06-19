package net.avdw.todo;

import net.avdw.todo.admin.initialize.InitializerCli;
import picocli.CommandLine;

@CommandLine.Command(name = "todo", description = "A tool to admin todo lists.",
        subcommands = {CommandLine.HelpCommand.class, InitializerCli.class})
public class TodoCli implements Runnable {
    public void run() {
        CommandLine.usage(TodoCli.class, System.out);
    }
}
