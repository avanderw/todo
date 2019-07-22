package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.Global;
import net.avdw.todo.repository.Local;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;

@Command(name = "todo",
        description = "The procrastination tool",
        subcommands = {
                HelpCommand.class,
                TodoStatus.class,
                TodoEdit.class
        })
public class Todo implements Runnable {
    @Option(names = {"-g", "--global"}, description = "Target the global directory")
    private boolean global;

    @Inject
    @Local
    private ARepository localRepository;

    @Inject
    @Global
    private ARepository globalRepository;

    public void run() {
        final ARepository repository = global ? globalRepository : localRepository;

        if (repository.exists()) {
            Console.info(String.format("Repository: %s", repository.getDirectory().toAbsolutePath()));
            CommandLine.usage(Todo.class, System.out);
        } else {
            System.out.println("No repository found (or any of the parent directories)");
            System.out.println(String.format("Use `todo%sinit` to start procrastinating", global ? " -g " : " "));
        }
    }

    public ARepository getRepository() {
        return global ? globalRepository : localRepository;
    }
}
