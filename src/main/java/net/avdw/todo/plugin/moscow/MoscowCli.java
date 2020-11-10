package net.avdw.todo.plugin.moscow;

import com.google.inject.Inject;
import net.avdw.todo.core.mixin.BooleanFilterMixin;
import net.avdw.todo.core.mixin.IndexSpecificationMixin;
import net.avdw.todo.core.view.TodoView;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

@Command(name = "moscow", resourceBundle = "messages", description = "${bundle:moscow.desc}", mixinStandardHelpOptions = true)
public class MoscowCli implements Runnable {
    @Inject private HasMoscow hasMoscow;
    @Mixin private IndexSpecificationMixin indexSpecificationMixin;
    @Mixin private BooleanFilterMixin booleanFilterMixin;
    @Spec private CommandSpec spec;
    @Inject private Repository<Integer, Todo> todoRepository;
    @Option(names = "--assign", descriptionKey = "moscow.type.desc")
    private MoscowType moscowType;
    @Inject private TodoView todoView;
    @Inject private MoscowMapper moscowMapper;
    @Inject private MoscowCleaner moscowCleaner;

    @Override
    public void run() {
        Specification<Integer, Todo> specification = new Any<>();
        if (indexSpecificationMixin.isActive()) {
            specification = indexSpecificationMixin;
        }

        if (booleanFilterMixin.isActive()) {
            specification = specification.and(booleanFilterMixin);
        }

        List<Todo> todoList = todoRepository.findAll(specification);
        if (moscowType == null) {
            Scanner scanner = new Scanner(System.in);
            todoList.forEach(todo -> {
                spec.commandLine().getOut().println(String.format("%nASSIGN: %s", todoView.render(todo)));
                String answer;
                if (hasMoscow.isSatisfiedBy(todo)) {
                    spec.commandLine().getOut().println(String.format("Currently assigned '%s' re-assign (y/n):", moscowMapper.map(todo).toUpperCase(Locale.ENGLISH)));
                    answer = scanner.next();
                } else {
                    answer = "y";
                }

                if (answer.toLowerCase(Locale.ENGLISH).equals("y")) {
                    spec.commandLine().getOut().println(String.format("%s",
                            Arrays.stream(MoscowType.values())
                                    .sorted(Comparator.naturalOrder())
                                    .map(type -> String.format("> %2s: %s", type.ordinal(), type))
                                    .collect(Collectors.joining("\n"))));
                    MoscowType moscowType = null;
                    boolean notAssigned = true;
                    while (notAssigned) {
                        spec.commandLine().getOut().print("Choice: ");
                        spec.commandLine().getOut().flush();
                        try {
                            String assign = scanner.next();
                            moscowType = MoscowType.values()[Integer.parseInt(assign)];
                            notAssigned = false;
                        } catch (NumberFormatException e) {
                            spec.commandLine().getOut().println("Bad option, chose again");
                            notAssigned = true;
                        }
                    }
                    String clean = moscowCleaner.clean(todo);
                    Todo newTodo = new Todo(todo.getId(), String.format("%s moscow:%s", clean, moscowType.name().toLowerCase(Locale.ENGLISH)));
                    todoRepository.update(newTodo);
                    spec.commandLine().getOut().println(todoView.render(newTodo));
                }
            });
        } else {
            todoRepository.setAutoCommit(false);
            todoList.forEach(todo -> {
                String clean = moscowCleaner.clean(todo);
                Todo newTodo = new Todo(todo.getId(), String.format("%s moscow:%s", clean, moscowType.name().toLowerCase(Locale.ENGLISH)));
                todoRepository.update(newTodo);
                spec.commandLine().getOut().println(todoView.render(newTodo));
            });
            todoRepository.commit();
        }
    }
}
