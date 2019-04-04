package net.avdw.todo.replace;

import net.avdw.todo.Config;
import net.avdw.todo.remove.RemoveFunc;
import picocli.CommandLine;

@CommandLine.Command(name = "replace", description = "Replace a todo item.")
public class ReplaceCli implements Runnable {
    @CommandLine.Parameters(description = "The index of the todo item.")
    Integer idx;

    @Override
    public void run() {
        new RemoveFunc(Config.TODO_FILE).remove(idx);
    }
}
