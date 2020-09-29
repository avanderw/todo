package net.avdw.todo;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.todo.domain.*;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import net.avdw.todo.style.StyleApplicator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Command(name = "archive", resourceBundle = "messages", description = "${bundle:archive}")
public class ArchiveCli implements Runnable {

    @Inject
    private Repository<Integer, Todo> todoRepository;
    @Inject
    private Path todoPath;
    @Spec
    private CommandSpec spec;
    @Inject
    private TemplatedResourceBundle templatedResourceBundle;
    private Gson gson = new Gson();
    @Inject
    private StyleApplicator styleApplicator;

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
                spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.TODO_LINE_ITEM,
                        String.format("{idx:'%3s',todo:'%s'}", i + 1, styleApplicator.apply(allTodoList.get(i).getText()))));
            }
        }

        todoRepository.setAutoCommit(false);
        archive(new FileRepository<>(todoPath.getParent().resolve("done.txt"), new TodoFileTypeBuilder()), isDone);
        archive(new FileRepository<>(todoPath.getParent().resolve("parked.txt"), new TodoFileTypeBuilder()), isParked);
        archive(new FileRepository<>(todoPath.getParent().resolve("removed.txt"), new TodoFileTypeBuilder()), isRemoved);
        todoRepository.commit();
    }

    private void archive(final Repository<Integer, Todo> archiveRepository, final Specification<Integer, Todo> specification) {
        List<Todo> archiveTodoList = todoRepository.findAll(specification);
        archiveRepository.addAll(archiveTodoList);
        todoRepository.removeAll(specification);
    }
}
