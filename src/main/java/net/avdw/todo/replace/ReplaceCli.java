package net.avdw.todo.replace;

import net.avdw.todo.Config;
import net.avdw.todo.Main;
import net.avdw.todo.remove.RemoveFunc;
import picocli.CommandLine;

@CommandLine.Command(name = "replace", description = "Replace a todo item.")
public class ReplaceCli implements Runnable {
    @CommandLine.Parameters(description = "The index of the todo item to replace.", index = "0", arity = "1")
    Integer idx;

    @CommandLine.Parameters(description = "New todo item.", index = "1", arity = "1")
    String todo;

    @Override
    public void run() {
        new ReplaceFunc(Config.TODO_FILE, Main.EVENT_BUS).replace(idx, todo);
    }
}
