package net.avdw.todo.action;

import picocli.CommandLine.Command;

@Command(name = "add", description = "Add an item to todo.txt")
public class TodoAdd implements Runnable {
    @Override
    public void run() {
        throw new UnsupportedOperationException("Implement backup first");
    }
}
