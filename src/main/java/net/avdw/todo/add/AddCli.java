package net.avdw.todo.add;

import com.google.inject.Inject;
import picocli.CommandLine;

@CommandLine.Command(name = "add", description = "Add a todo item.")
public class AddCli implements Runnable {
    @CommandLine.Parameters(description = "The todo line item.")
    String todo;

    @Inject private AddApi addApi;

    @Override
    public void run() {
        addApi.add(todo);
    }
}
