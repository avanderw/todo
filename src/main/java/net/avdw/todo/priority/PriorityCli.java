package net.avdw.todo.priority;

import net.avdw.todo.Config;
import net.avdw.todo.Main;
import picocli.CommandLine;

@CommandLine.Command(name = "pri", description = "Prioritise todo items.")
public class PriorityCli implements Runnable {
    @CommandLine.Option(names = "-r", description = "Remove priority from the todo item.")
    boolean remove;

    @CommandLine.Parameters(description = "The index of the todo item.", index = "0", arity = "1")
    Integer idx;

    @CommandLine.Parameters(defaultValue = "A",
            description = "The priority value of the todo item (default: ${DEFAULT-VALUE}). Valid values: ${COMPLETION-CANDIDATES}",
            index = "1", arity = "1")
    PriorityInput priority;


    @Override
    public void run() {
        if (remove) {
            new PriorityFunc(Config.TODO_FILE, Main.EVENT_BUS).remove(idx);
        } else {
            new PriorityFunc(Config.TODO_FILE, Main.EVENT_BUS).add(idx, priority.name());
        }
    }
}