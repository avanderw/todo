package net.avdw.todo.core;

import net.avdw.todo.core.selector.ExtLoader;
import net.avdw.todo.core.selector.Selector;
import net.avdw.todo.core.view.TodoListView;
import net.avdw.todo.domain.IsDone;
import net.avdw.todo.domain.IsParked;
import net.avdw.todo.domain.IsPriority;
import net.avdw.todo.domain.IsRemoved;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Command(name = "sort", resourceBundle = "messages", description = "${bundle:sort}")
public class SortCli implements Runnable {
    private final Path todoPath;
    private final Repository<Integer, Todo> todoRepository;
    private final TodoListView todoListView;
    private final Set<Selector> selectorSet;
    private final ExtLoader extLoader;
    @Spec private CommandSpec spec;
    @Option(names = "--by", description = "e.g. plugin + ext: - ext:") private String sortFunc;

    @Inject
    SortCli(final Path todoPath, final Repository<Integer, Todo> todoRepository, final TodoListView todoListView, final Set<Selector> selectorSet, final ExtLoader extLoader) {
        this.todoPath = todoPath;
        this.todoRepository = todoRepository;
        this.todoListView = todoListView;
        this.selectorSet = selectorSet;
        this.extLoader = extLoader;
    }

    @Override
    public void run() {
        final Comparator<Todo> evalFunc;
        if (sortFunc == null || sortFunc.isBlank()) {
            evalFunc = Comparator.naturalOrder();
        } else {
            final Set<Selector> allSelectors = new HashSet<>(selectorSet);
            allSelectors.addAll(extLoader.fromFunction(sortFunc));
            final TodoEvaluator todoEvaluator = new TodoEvaluator(sortFunc, allSelectors);
            evalFunc = Comparator.comparingInt(todoEvaluator::evaluate);
        }

        final Specification<Integer, Todo> any = new Any<>();
        final Specification<Integer, Todo> priority = new IsPriority();
        final Specification<Integer, Todo> done = new IsDone().or(new IsParked()).or(new IsRemoved());

        final Repository<Integer, Todo> sortedRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        sortedRepository.setAutoCommit(false);
        sortedRepository.removeAll(any);
        sortedRepository.addAll(todoRepository.findAll(priority).stream()
                .sorted(Comparator.comparing(Todo::getText))
                .collect(Collectors.toList()));
        sortedRepository.addAll(todoRepository.findAll(new Any<Integer, Todo>().not(priority).not(done)).stream()
                .sorted(evalFunc.reversed())
                .collect(Collectors.toList()));
        sortedRepository.addAll(todoRepository.findAll(done).stream()
                .sorted(Comparator.comparing(Todo::getText))
                .collect(Collectors.toList()));
        sortedRepository.commit();

        spec.commandLine().getOut().println(todoListView.render(sortedRepository.findAll(any), sortedRepository, 0));
    }
}
