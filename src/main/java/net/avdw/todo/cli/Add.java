package net.avdw.todo.cli;

import picocli.CommandLine;

@CommandLine.Command(name = "add", description = "Add a todo item.")
public class Add implements Runnable {
    @CommandLine.Parameters(description = "The todo line item.")
    String todo;

    @Override
    public void run() {

    }
}
