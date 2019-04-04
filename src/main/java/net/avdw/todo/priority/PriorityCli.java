package net.avdw.todo.priority;

import net.avdw.todo.Config;
import net.avdw.todo.remove.RemoveFunc;
import picocli.CommandLine;

@CommandLine.Command(name = "pri", description = "Prioritise todo items.")
public class PriorityCli implements Runnable {
    @CommandLine.Parameters(description = "The index of the todo item.")
    Integer idx;

    @CommandLine.Parameters(description = "The priority of the todo item")
    String priority;

    @Override
    public void run() {
        new RemoveFunc(Config.TODO_FILE).remove(idx);
    }
}
