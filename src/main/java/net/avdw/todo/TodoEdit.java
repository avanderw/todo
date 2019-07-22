package net.avdw.todo;

import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "edit")
public class TodoEdit implements Runnable {
    @ParentCommand
    private Todo todo;

    @Override
    public void run() {
        Console.info(String.format("Edit %s", todo.getRepository().getTodoFile()));
        todo.getRepository().edit();
    }
}
