package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.repository.ARepository;
import picocli.CommandLine.Command;

@Command(name = "status")
public class TodoStatus implements Runnable {

    @Inject
    private ARepository repository;

    @Override
    public void run() {
        if (repository.exists()) {
            System.out.println(String.format("repository: %s", repository.getPath().toAbsolutePath()));
        } else {
            System.out.println("no repository found (or any of the parent directories)");
            System.out.println("use `todo init` to start procrastinating");
        }
    }
}
