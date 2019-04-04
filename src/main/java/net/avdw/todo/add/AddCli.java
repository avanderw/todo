package net.avdw.todo.add;

import net.avdw.todo.Config;
import net.avdw.todo.Main;
import picocli.CommandLine;

@CommandLine.Command(name = "add", description = "Add a todo item.")
public class AddCli implements Runnable {
    @CommandLine.Parameters(description = "The todo line item.")
    String todo;

    @Override
    public void run() {
        new AddFunc(Config.TODO_FILE, Main.EVENT_BUS).add(todo);
    }
}
