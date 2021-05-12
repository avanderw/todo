package net.avdw.todo.extension.moscow;

import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
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

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

@Command(name = "moscow", resourceBundle = "messages", description = "${bundle:moscow.desc}", mixinStandardHelpOptions = true)
public class MoscowCli implements Runnable {
    private final HasMoscow hasMoscow;
    private final Repository<Integer, Todo> todoRepository;
    private final TodoView todoView;
    private final MoscowMapper moscowMapper;
    private final MoscowCleaner moscowCleaner;
    private final TemplatedResource templatedResource;
    @Mixin private IndexFilterMixin indexSpecificationMixin;
    @Mixin private BooleanFilterMixin booleanFilterMixin;
    @Spec private CommandSpec spec;
    @Option(names = "--assign", descriptionKey = "moscow.type.desc")
    private MoscowType moscowType;

    @Inject
    MoscowCli(final HasMoscow hasMoscow, final Repository<Integer, Todo> todoRepository, final TodoView todoView, final MoscowMapper moscowMapper, final MoscowCleaner moscowCleaner, final TemplatedResource templatedResource) {
        this.hasMoscow = hasMoscow;
        this.todoRepository = todoRepository;
        this.todoView = todoView;
        this.moscowMapper = moscowMapper;
        this.moscowCleaner = moscowCleaner;
        this.templatedResource = templatedResource;
    }

    @Override
    public void run() {
        Specification<Integer, Todo> specification = new Any<>();
        if (indexSpecificationMixin.isActive()) {
            specification = indexSpecificationMixin;
        }

        if (booleanFilterMixin.isActive()) {
            specification = specification.and(booleanFilterMixin);
        }

        final List<Todo> todoList = todoRepository.findAll(specification);

        if (todoList.isEmpty()) {
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.NO_TODO_FOUND));
            return;
        }

        if (moscowType == null) {
            final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
            todoList.forEach(todo -> {
                spec.commandLine().getOut().println(String.format("%nASSIGN: %s", todoView.render(todo)));
                final String answer;
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
                            final String assign = scanner.next();
                            moscowType = MoscowType.values()[Integer.parseInt(assign)];
                            notAssigned = false;
                        } catch (final NumberFormatException e) {
                            spec.commandLine().getOut().println("Bad option, chose again");
                            notAssigned = true;
                        }
                    }
                    final String clean = moscowCleaner.clean(todo);
                    final Todo newTodo = new Todo(todo.getId(), String.format("%s moscow:%s", clean, moscowType.name().toLowerCase(Locale.ENGLISH)));
                    todoRepository.update(newTodo);
                    spec.commandLine().getOut().println(todoView.render(newTodo));
                }
            });
        } else {
            todoRepository.setAutoCommit(false);
            todoList.forEach(todo -> {
                final String clean = moscowCleaner.clean(todo);
                final Todo newTodo = new Todo(todo.getId(), String.format("%s moscow:%s", clean, moscowType.name().toLowerCase(Locale.ENGLISH)));
                todoRepository.update(newTodo);
                spec.commandLine().getOut().println(todoView.render(newTodo));
            });
            todoRepository.commit();
        }
    }
}
