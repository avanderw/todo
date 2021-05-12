package net.avdw.todo.extension.size;

import net.avdw.todo.core.mixin.BooleanFilterMixin;
import net.avdw.todo.core.mixin.IndexFilterMixin;
import net.avdw.todo.core.view.TodoView;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoTextCleaner;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

@Command(name = "size", resourceBundle = "messages", description = "${bundle:size.desc}", mixinStandardHelpOptions = true)
public class SizeCli implements Runnable {
    private final HasSize hasSize;
    private final Repository<Integer, Todo> todoRepository;
    private final TodoView todoView;
    private final TodoTextCleaner todoTextCleaner;
    private final SizeMapper sizeMapper;
    private final SizeCleaner sizeCleaner;
    private final SizeGroup sizeGroup;
    private final Random random = new Random();
    @Mixin private IndexFilterMixin indexSpecificationMixin;
    @Mixin private BooleanFilterMixin booleanFilterMixin;
    @Spec private CommandSpec spec;
    @Option(names = "--assign", descriptionKey = "size.type.desc")
    private Integer size;

    @Inject
    SizeCli(final HasSize hasSize, final Repository<Integer, Todo> todoRepository, final TodoView todoView, final TodoTextCleaner todoTextCleaner, final SizeMapper sizeMapper, final SizeCleaner sizeCleaner, final SizeGroup sizeGroup) {
        this.hasSize = hasSize;
        this.todoRepository = todoRepository;
        this.todoView = todoView;
        this.todoTextCleaner = todoTextCleaner;
        this.sizeMapper = sizeMapper;
        this.sizeCleaner = sizeCleaner;
        this.sizeGroup = sizeGroup;
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
            spec.commandLine().getOut().println("No todos to size");
        }
        if (size == null) {
            final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
            todoList.forEach(todo -> {
                spec.commandLine().getOut().println("");
                final Map<String, List<Todo>> sizeGroupMap = todoRepository.findAll(hasSize).stream().collect(Collectors.groupingBy(sizeGroup.collector()));
                sizeGroupMap.forEach((key, list) -> spec.commandLine().getOut().println(String.format("     SIZE %2s: %s", key, todoTextCleaner.clean(list.get(random.nextInt(list.size()))))));
                spec.commandLine().getOut().println(String.format("      ASSIGN: %s", todoView.render(todo)));
                final String answer;
                if (hasSize.isSatisfiedBy(todo)) {
                    spec.commandLine().getOut().println(String.format("Currently assigned '%s' re-assign (y/n):", sizeMapper.map(todo)));
                    answer = scanner.next();
                } else {
                    answer = "y";
                }

                if (answer.toLowerCase(Locale.ENGLISH).equals("y")) {
                    Integer assignSize = null;
                    boolean notAssigned = true;
                    while (notAssigned) {
                        spec.commandLine().getOut().print("ENTER (size): ");
                        spec.commandLine().getOut().flush();
                        try {
                            final String assign = scanner.next();
                            assignSize = Integer.parseInt(assign);
                            notAssigned = false;
                        } catch (final NumberFormatException e) {
                            spec.commandLine().getOut().println("Not a number, please enter a number: ");
                            notAssigned = true;
                        }
                    }
                    final String clean = sizeCleaner.clean(todo);
                    final Todo newTodo = new Todo(todo.getId(), String.format("%s size:%s", clean, assignSize));
                    todoRepository.update(newTodo);
                    spec.commandLine().getOut().println(todoView.render(newTodo));
                }
            });
        } else {
            todoRepository.setAutoCommit(false);
            todoList.forEach(todo -> {
                final String clean = sizeCleaner.clean(todo);
                final Todo newTodo = new Todo(todo.getId(), String.format("%s size:%s", clean, size));
                todoRepository.update(newTodo);
                spec.commandLine().getOut().println(todoView.render(newTodo));
            });
            todoRepository.commit();
        }
    }
}
