package net.avdw.todo.list.addition;

import com.google.inject.Inject;
import picocli.CommandLine;

@CommandLine.Command(name = "add", description = "Add a todo item.")
public class AddCli implements Runnable {
    @CommandLine.Parameters(description = "The todo line item.")
    private String todo;

    @Inject private AListAddition listAddition;

    @Override
    public void run() {
        listAddition.add(todo);
    }
}
