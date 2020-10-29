package net.avdw.todo.core;

import com.google.inject.Inject;
import net.avdw.todo.domain.Priority;
import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.domain.IsContaining;
import net.avdw.todo.domain.IsPriority;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import net.avdw.todo.style.TodoStyler;
import picocli.CommandLine.Command;
import picocli.CommandLine.IExitCodeGenerator;
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
public
class AddCli implements Runnable, IExitCodeGenerator {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Parameters(description = "Item to append to todo.txt", arity = "1")
    private String addition;
    private int exitCode = 0;
    @Option(names = {"-p", "--priority"}, description = "Prioritise addition with the next highest available priority")
    private boolean hasPriority = false;
    @Spec private CommandSpec spec;
    @Inject private TodoStyler todoStyler;
    @Inject private TemplatedResource templatedResource;
    @Inject private Repository<Integer, Todo> todoRepository;

    @Override
    public int getExitCode() {
        return exitCode;
    }

    @Override
    public void run() {
        Specification<Integer, Todo> containingAddition = new IsContaining(addition);
        if (!todoRepository.findAll(containingAddition).isEmpty()) {
            spec.commandLine().getErr().println(templatedResource.populateKey(ResourceBundleKey.ADD_DUPLICATE));
            exitCode = 1;
            return;
        }

        addition = String.format("%s %s", simpleDateFormat.format(new Date()), addition);
        if (hasPriority) {
            Specification<Integer, Todo> withPriority = new IsPriority();
            List<Priority> usedPriorityList = todoRepository.findAll(withPriority).stream().map(Todo::getPriority).collect(Collectors.toList());
            Priority priority = Arrays.stream(Priority.values()).filter(pri -> !usedPriorityList.contains(pri)).sorted().findFirst().orElse(Priority.Z);
            addition = String.format("(%s) %s", priority, addition);
        }

        Todo todo = new Todo(todoRepository.size(), addition);
        todoRepository.add(todo);
        spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                String.format("{idx:'%3s',todo:\"%s\"}", todo.getIdx(), todoStyler.style(todo).replaceAll("\"", "\\\\\""))));
    }
}