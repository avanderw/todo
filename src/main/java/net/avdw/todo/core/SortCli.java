package net.avdw.todo.core;

import com.google.inject.Inject;
import net.avdw.todo.core.selector.ExtLoader;
import net.avdw.todo.core.selector.Selector;
import net.avdw.todo.core.view.TodoListView;
import net.avdw.todo.domain.*;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Command(name = "sort", resourceBundle = "messages", description = "${bundle:sort}")
public class SortCli implements Runnable {
    @Spec private CommandSpec spec;
    @Option(names = "--by", description = "e.g. plugin + ext: - ext:") private String sortFunc;
    @Inject private Path todoPath;
    @Inject private Repository<Integer, Todo> todoRepository;
    @Inject private TodoListView todoListView;
    @Inject private Set<Selector> selectorSet;
    @Inject private ExtLoader extLoader;

    @Override
    public void run() {
        Comparator<Todo> evalFunc;
        if (sortFunc == null || sortFunc.isBlank()) {
            evalFunc = Comparator.naturalOrder();
        } else {
            Set<Selector> allSelectors = new HashSet<>(selectorSet);
            allSelectors.addAll(extLoader.fromFunction(sortFunc));
            TodoEvaluator todoEvaluator = new TodoEvaluator(sortFunc, allSelectors);
            evalFunc = Comparator.comparingInt(todoEvaluator::evaluate);
        }

        Specification<Integer, Todo> any = new Any<>();
        Specification<Integer, Todo> priority = new IsPriority();
        Specification<Integer, Todo> done = new IsDone().or(new IsParked()).or(new IsRemoved());

        Repository<Integer, Todo> sortedRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
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
