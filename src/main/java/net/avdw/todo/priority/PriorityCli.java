package net.avdw.todo.priority;

import com.google.inject.Inject;
import picocli.CommandLine;

@CommandLine.Command(name = "pri", description = "Prioritise todo items.")
public class PriorityCli implements Runnable {
    @CommandLine.Option(names = "-r", description = "Remove priority from the todo item.")
    private
    boolean remove;

    @CommandLine.Parameters(description = "The index of the todo item.", index = "0", arity = "1")
    private
    Integer idx;

    @CommandLine.Parameters(defaultValue = "A",
            description = "The priority value of the todo item (default: ${DEFAULT-VALUE}). Valid values: ${COMPLETION-CANDIDATES}",
            index = "1", arity = "1")
    private
    PriorityInput priority;

    @Inject
    private
    PriorityApi priorityApi;

    @Override
    public void run() {
        if (remove) {
            priorityApi.remove(idx);
        } else {
            priorityApi.add(idx, priority);
        }
    }
}
