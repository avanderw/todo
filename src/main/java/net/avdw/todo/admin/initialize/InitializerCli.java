package net.avdw.todo.admin.initialize;

import com.google.inject.Inject;
import picocli.CommandLine;

import java.nio.file.Path;

@CommandLine.Command(name = "initialise", aliases = "init", description = "Initialise a task repository at the specified location.")
public class InitializerCli implements Runnable {
    @CommandLine.Parameters(arity = "0..1", description = "Path to repository.")
    private Path repositoryPath;

    @Inject
    private AInitializer aInitializer;

    @Override
    public void run() {
        if (repositoryPath == null) {
            aInitializer.init();
        } else {
            aInitializer.init(repositoryPath);
        }
    }
}
