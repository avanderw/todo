package net.avdw.todo.done;

import com.google.inject.Inject;
import net.avdw.todo.Config;
import net.avdw.todo.Main;
import picocli.CommandLine;

@CommandLine.Command(name = "do", description = "Completes a todo item.")
public class DoneCli implements Runnable {
    @CommandLine.Parameters(description = "The index of the todo item.")
    Integer idx;

    @Inject
    DoneApi doneApi;

    @Override
    public void run() {
        doneApi.done(idx);
    }
}
