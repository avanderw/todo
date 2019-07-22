package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.Global;
import net.avdw.todo.repository.Local;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "status")
public class TodoStatus implements Runnable {

    @Inject
    @Global
    private ARepository globalRepository;

    @Inject
    @Local
    private ARepository localRepository;


    @Override
    public void run() {
        Console.h1("Todo Status");
        Console.info(String.format("Default: %s", localRepository.exists() ? localRepository.getPath().toAbsolutePath() : "todo init"));
        Console.info(String.format("Global : %s", globalRepository.exists() ? globalRepository.getPath().toAbsolutePath() : "todo init --global"));

        if (!localRepository.exists() || !globalRepository.exists()) {
            CommandLine.usage(TodoInit.class, System.out);
        }
    }
}
