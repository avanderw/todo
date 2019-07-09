package net.avdw.todo.list.addition;

import com.google.inject.Inject;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.file.FileTask;
import net.avdw.todo.repository.model.ATask;
import picocli.CommandLine;

import java.nio.file.Path;

@CommandLine.Command(name = "add", description = "Add a todo item.")
public class AdditionCli implements Runnable {
    @CommandLine.Option(names = {"-r", "--repository"}, description = "The repository to add the task to.")
    private Path repositoryPath;

    @CommandLine.Parameters(description = "The task to add.")
    private String todo;

    @Inject
    private AAddition listAddition;

    @Inject
    @FileTask
    private ARepository<ATask> fileTaskRepository;

    @Override
    public void run() {
        if (repositoryPath != null) {
            fileTaskRepository.setRepositoryPath(repositoryPath);
        }

        listAddition.add(todo);
    }
}
