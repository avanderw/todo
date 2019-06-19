package net.avdw.todo.list.removal;

import com.google.inject.Inject;
import picocli.CommandLine;

@CommandLine.Command(name = "remove", aliases = {"del", "rm"}, description = "Remove a todo item.")
public class RemoveCli implements Runnable {
    @CommandLine.Parameters(description = "The index of the todo item.")
    Integer idx;

    @Inject
    private RemoveApi removeApi;

    @Override
    public void run() {
        removeApi.remove(idx);
    }
}