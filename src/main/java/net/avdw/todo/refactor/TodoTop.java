package net.avdw.todo.refactor;

import picocli.CommandLine;

@CommandLine.Command(name = "top", description = "List the top items in todo.txt")
public class TodoTop implements Runnable {

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
    }
}
