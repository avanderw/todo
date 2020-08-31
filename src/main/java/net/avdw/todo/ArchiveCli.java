package net.avdw.todo;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.todo.domain.*;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Command(name = "archive", description = "Archive done, removed and parked items")
public class ArchiveCli implements Runnable {

    @Inject
    private Repository<Todo> todoRepository;
    @Inject
    private Path todoPath;
    @Spec
    private CommandSpec spec;
    @Inject
    private TemplatedResourceBundle templatedResourceBundle;
    private Gson gson = new Gson();

    @Override
    public void run() {
        Specification<Todo> isDone = new IsDone();
        Specification<Todo> isParked = new IsParked();
        Specification<Todo> isRemoved = new IsRemoved();
        Specification<Todo> isArchive = isDone.or(isParked).or(isRemoved);
        List<Todo> allTodoList= todoRepository.findAll();
        for (int i = 0; i < allTodoList.size(); i++) {
            if (isArchive.isSatisfiedBy(allTodoList.get(i))) {
                spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.TODO_LINE_ITEM,
                        gson.fromJson(String.format("{idx:'%3s',todo:'%s'}", i + 1, allTodoList.get(i)), Map.class)));
            }
        }

        todoRepository.setAutoCommit(false);
        archive(new FileRepository<>(todoPath.getParent().resolve("done.txt"), new TodoBuilder()), isDone);
        archive(new FileRepository<>(todoPath.getParent().resolve("parked.txt"), new TodoBuilder()), isParked);
        archive(new FileRepository<>(todoPath.getParent().resolve("removed.txt"), new TodoBuilder()), isRemoved);
        todoRepository.commit();
    }

    private void archive(final Repository<Todo> archiveRepository, final Specification<Todo> specification) {
        List<Todo> archiveTodoList = todoRepository.findAll(specification);
        archiveRepository.addAll(archiveTodoList);
        todoRepository.removeAll(specification);
    }
}
