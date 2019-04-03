package net.avdw.todo.remove;

import net.avdw.todo.Config;
import picocli.CommandLine;

@CommandLine.Command(name = "rm", description = "Remove a todo item.")
public class RemoveCli implements Runnable {
    @CommandLine.Parameters(description = "The index of the todo item.")
    Integer idx;

    @Override
    public void run() {
        new RemoveFunc(Config.TODO_FILE).remove(idx);
    }
}
