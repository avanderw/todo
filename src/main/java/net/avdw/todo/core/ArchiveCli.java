package net.avdw.todo.core;

import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.SuppressFBWarnings;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.style.TodoStyler;
import net.avdw.todo.domain.IsDone;
import net.avdw.todo.domain.IsParked;
import net.avdw.todo.domain.IsRemoved;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.List;

@Command(name = "archive", resourceBundle = "messages", description = "${bundle:archive}")
public class ArchiveCli implements Runnable {

    private final TodoStyler todoStyler;
    private final TemplatedResource templatedResource;
    private final Path todoPath;
    private final Repository<Integer, Todo> todoRepository;
    @Spec private CommandSpec spec;

    @Inject
    ArchiveCli(final TodoStyler todoStyler, final TemplatedResource templatedResource, final Path todoPath, final Repository<Integer, Todo> todoRepository) {
        this.todoStyler = todoStyler;
        this.templatedResource = templatedResource;
        this.todoPath = todoPath;
        this.todoRepository = todoRepository;
    }

    private void archive(final Repository<Integer, Todo> archiveRepository, final Specification<Integer, Todo> specification) {
        final List<Todo> archiveTodoList = todoRepository.findAll(specification);
        archiveRepository.addAll(archiveTodoList);
        todoRepository.removeAll(specification);
    }

    @Override
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
            justification = "Google Guice does not allow for null injection (todoPath)")
    public void run() {

        final Specification<Integer, Todo> isDone = new IsDone();
        final Specification<Integer, Todo> isParked = new IsParked();
        final Specification<Integer, Todo> isRemoved = new IsRemoved();
        final Specification<Integer, Todo> isArchive = isDone.or(isParked).or(isRemoved);
        final List<Todo> allTodoList = todoRepository.findAll(new Any<>());
        for (int i = 0; i < allTodoList.size(); i++) {
            if (isArchive.isSatisfiedBy(allTodoList.get(i))) {
                spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                        String.format("{idx:'%3s',todo:\"%s\"}", i + 1, todoStyler.style(allTodoList.get(i)).replaceAll("\"", "\\\\\""))));
            }
        }

        todoRepository.setAutoCommit(false);
        archive(new FileRepository<>(todoPath.getParent().resolve("done.txt"), new TodoFileTypeBuilder()), isDone);
        archive(new FileRepository<>(todoPath.getParent().resolve("parked.txt"), new TodoFileTypeBuilder()), isParked);
        archive(new FileRepository<>(todoPath.getParent().resolve("removed.txt"), new TodoFileTypeBuilder()), isRemoved);
        todoRepository.commit();
    }
}
