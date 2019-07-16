package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.repository.ARepository;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;

@Command(name = "todo",
        description = "the procrastination tool",
        subcommands = {
                HelpCommand.class,
                TodoStatus.class
        })
public class Todo implements Runnable {
    @Inject
    private ARepository repository;

    public void run() {
        if (repository.exists()) {
            CommandLine.usage(Todo.class, System.out);
        } else {
            System.out.println("no repository found (or any of the parent directories)");
            System.out.println("use `todo init` to start procrastinating");
        }
    }
}
