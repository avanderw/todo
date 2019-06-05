package net.avdw.todo.list.completion;

import com.google.inject.Inject;
import picocli.CommandLine;

@CommandLine.Command(name = "completion", aliases = "do", description = "Completes a todo item.")
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
