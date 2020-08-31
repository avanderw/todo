package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.domain.IsContaining;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.IsPriority;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Command(name = "add", description = "Add an item to todo.txt")
class AddCli implements Runnable {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Parameters(description = "Item to append to todo.txt", arity = "1")
    private String addition;

    @Option(names = {"-p", "--priority"}, description = "Prioritise addition with the next highest available priority")
    private boolean hasPriority = false;

    @Inject
    private Repository<Integer, Todo> todoRepository;

    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        Specification<Integer, Todo> containingAddition = new IsContaining(addition);
        if (!todoRepository.findAll(containingAddition).isEmpty()) {
            spec.commandLine().getOut().println("duplicate");
            throw new UnsupportedOperationException();
        }

        addition = String.format("%s %s", SIMPLE_DATE_FORMAT.format(new Date()), addition);
        if (hasPriority) {
            Specification<Integer, Todo> withPriority = new IsPriority();
            List<Priority> usedPriorityList = todoRepository.findAll(withPriority).stream().map(Todo::getPriority).collect(Collectors.toList());
            Priority priority = Arrays.stream(Priority.values()).filter(pri -> !usedPriorityList.contains(pri)).sorted().findFirst().orElse(Priority.Z);
            addition = String.format("%s %s", priority, addition);
        }

        todoRepository.add(new Todo(todoRepository.size(), addition));
    }
}
