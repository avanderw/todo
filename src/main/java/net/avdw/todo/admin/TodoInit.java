package net.avdw.todo.admin;

import picocli.CommandLine.Command;

@Command(name = "init", description = "Initialize .todo directory")
public class TodoInit implements Runnable {

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
