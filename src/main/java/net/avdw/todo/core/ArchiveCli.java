package net.avdw.todo.core;

import com.google.inject.Inject;
import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.SuppressFBWarnings;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.domain.IsDone;
import net.avdw.todo.domain.IsParked;
import net.avdw.todo.domain.IsRemoved;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import net.avdw.todo.core.style.TodoStyler;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.nio.file.Path;
import java.util.List;

@Command(name = "archive", resourceBundle = "messages", description = "${bundle:archive}")
public class ArchiveCli implements Runnable {

    @Spec private CommandSpec spec;
    @Inject private TodoStyler todoStyler;
    @Inject private TemplatedResource templatedResource;
    @Inject private Path todoPath;
    @Inject private Repository<Integer, Todo> todoRepository;

    private void archive(final Repository<Integer, Todo> archiveRepository, final Specification<Integer, Todo> specification) {
        List<Todo> archiveTodoList = todoRepository.findAll(specification);
        archiveRepository.addAll(archiveTodoList);
        todoRepository.removeAll(specification);
    }

    @Override
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
            justification = "Google Guice does not allow for null injection (todoPath)")
    public void run() {

        Specification<Integer, Todo> isDone = new IsDone();
        Specification<Integer, Todo> isParked = new IsParked();
        Specification<Integer, Todo> isRemoved = new IsRemoved();
        Specification<Integer, Todo> isArchive = isDone.or(isParked).or(isRemoved);
        List<Todo> allTodoList = todoRepository.findAll(new Any<>());
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
