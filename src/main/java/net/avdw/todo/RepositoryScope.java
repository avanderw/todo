package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.nio.file.Paths;

public class RepositoryScope {
    private final Repository<Integer, Todo> todoRepository;
    private final Path todoPath;
    @Option(names = "--incl-done", descriptionKey = "repository.incl.done.desc")
    private boolean inclDone = false;
    @Option(names = "--incl-parked", descriptionKey = "repository.incl.parked.desc")
    private boolean inclParked = false;
    @Option(names = "--incl-removed", descriptionKey = "repository.incl.removed.desc")
    private boolean inclRemoved = false;

    @Inject
    RepositoryScope(final Repository<Integer, Todo> todoRepository, final Path todoPath) {
        this.todoRepository = todoRepository;
        this.todoPath = todoPath;
    }

    public Repository<Integer, Todo> allRepositories() {
        Repository<Integer, Todo> allRepositories = new FileRepository<>(Paths.get("scoped-repository.txt"), new TodoFileTypeBuilder());
        allRepositories.setAutoCommit(false);
        allRepositories.addAll(todoRepository.findAll(new Any<>()));

        if (inclDone) {
            allRepositories.addAll(new FileRepository<>(todoPath.getParent().resolve("done.txt"), new TodoFileTypeBuilder()).findAll(new Any<>()));
        }
        if (inclRemoved) {
            allRepositories.addAll(new FileRepository<>(todoPath.getParent().resolve("removed.txt"), new TodoFileTypeBuilder()).findAll(new Any<>()));
        }
        if (inclParked) {
            allRepositories.addAll(new FileRepository<>(todoPath.getParent().resolve("parked.txt"), new TodoFileTypeBuilder()).findAll(new Any<>()));
        }
        return allRepositories;
    }
}
