package net.avdw.todo.plugin.plan;

import com.google.inject.Inject;
import net.avdw.todo.core.mixin.BooleanFilterMixin;
import net.avdw.todo.core.mixin.IndexFilterMixin;
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

@Command(name = "plan", resourceBundle = "messages", description = "${bundle:plan.desc}", mixinStandardHelpOptions = true)
public class PlanCli implements Runnable {
    @Mixin private BooleanFilterMixin booleanFilterMixin;
    @Inject private HasPlan hasPlan;
    @Mixin private IndexFilterMixin indexSpecificationMixin;
    @Inject private PlanCleaner planCleaner;
    @Inject private PlanExt planExt;
    @Inject private PlanMapper planMapper;
    @Option(names = "--assign", descriptionKey = "plan.type.desc")
    private PlanType planType;
    @Spec private CommandSpec spec;
    @Inject private Repository<Integer, Todo> todoRepository;
    @Inject private TodoView todoView;

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

        if (todoList.isEmpty()) {
            spec.commandLine().getOut().println("No todos to plan");
        }
        if (planType == null) {
            Scanner scanner = new Scanner(System.in);
            todoList.forEach(todo -> {
                spec.commandLine().getOut().println(String.format("%nASSIGN: %s", todoView.render(todo)));
                String answer;
                if (hasPlan.isSatisfiedBy(todo)) {
                    spec.commandLine().getOut().println(String.format("Currently assigned '%s' re-assign (y/n):", planMapper.map(todo).toUpperCase(Locale.ENGLISH)));
                    answer = scanner.next();
                } else {
                    answer = "y";
                }

                if (answer.toLowerCase(Locale.ENGLISH).equals("y")) {
                    spec.commandLine().getOut().println(String.format("%s",
                            Arrays.stream(PlanType.values())
                                    .sorted(Comparator.naturalOrder())
                                    .map(type -> String.format("> %2s: %s", type.ordinal(), type))
                                    .collect(Collectors.joining("\n"))));
                    PlanType planType = null;
                    boolean notAssigned = true;
                    while (notAssigned) {
                        spec.commandLine().getOut().print("Choice: ");
                        spec.commandLine().getOut().flush();
                        try {
                            String assign = scanner.next();
                            planType = PlanType.values()[Integer.parseInt(assign)];
                            notAssigned = false;
                        } catch (NumberFormatException e) {
                            spec.commandLine().getOut().println("Bad option, chose again");
                            notAssigned = true;
                        }
                    }
                    String clean = planCleaner.clean(todo);
                    Todo newTodo = new Todo(todo.getId(), String.format("%s %s:%s", clean, planExt.preferredExt(), planType.name().toLowerCase(Locale.ENGLISH)));
                    todoRepository.update(newTodo);
                    spec.commandLine().getOut().println(todoView.render(newTodo));
                }
            });
        } else {
            todoRepository.setAutoCommit(false);
            todoList.forEach(todo -> {
                String clean = planCleaner.clean(todo);
                Todo newTodo = new Todo(todo.getId(), String.format("%s %s:%s", clean, planExt.preferredExt(), planType.name().toLowerCase(Locale.ENGLISH)));
                todoRepository.update(newTodo);
                spec.commandLine().getOut().println(todoView.render(newTodo));
            });
            todoRepository.commit();
        }
    }
}
