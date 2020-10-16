package net.avdw.todo.action;

import picocli.CommandLine.Command;

@Command(name = "browse", resourceBundle = "browse", description = "${bundle:description}", mixinStandardHelpOptions = true)
public class BrowseCli implements Runnable {

    @Override
    public void run() {

    }
}
