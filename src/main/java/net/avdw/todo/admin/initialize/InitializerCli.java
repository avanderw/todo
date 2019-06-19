package net.avdw.todo.admin.initialize;

import com.google.inject.Inject;
import picocli.CommandLine;

@CommandLine.Command(name = "initialise", description = "Create a list.")
public class InitializerCli implements Runnable {

    @Inject
    private AInitializer todoInitializer;

    @Override
    public void run() {
        todoInitializer.init();
    }
}
