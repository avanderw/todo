package net.avdw.todo.list;

import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "list", description = "List the todo.txt items.")
public class ListCli implements Runnable {
    @CommandLine.Parameters
    List<String> filters;

    @Override
    public void run() {

    }
}
