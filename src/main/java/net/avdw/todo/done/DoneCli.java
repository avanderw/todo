package net.avdw.todo.done;

import net.avdw.todo.Config;
import net.avdw.todo.Main;
import picocli.CommandLine;

@CommandLine.Command(name = "do", description = "Completes a todo item.")
public class DoneCli implements Runnable {
    @CommandLine.Parameters(description = "The index of the todo item.")
    Integer idx;

    @Override
    public void run() {
        new DoneFunc(Config.TODO_FILE, Main.EVENT_BUS).done(idx);
    }
}
