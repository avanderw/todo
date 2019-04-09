package net.avdw.todo.replace;

import com.google.inject.Inject;
import net.avdw.todo.Config;
import net.avdw.todo.Main;
import net.avdw.todo.add.AddApi;
import net.avdw.todo.remove.RemoveFunc;
import picocli.CommandLine;

@CommandLine.Command(name = "replace", description = "Replace a todo item.")
public class ReplaceCli implements Runnable {
    @CommandLine.Parameters(description = "The index of the todo item to replace.", index = "0", arity = "1")
    Integer idx;

    @CommandLine.Parameters(description = "New todo item.", index = "1", arity = "1")
    String todo;

    @Inject
    private ReplaceApi replaceApi;

    @Override
    public void run() {
        replaceApi.replace(idx, todo);
    }
}
