package net.avdw.todo.plugin.size;

import com.google.inject.Inject;
import net.avdw.todo.core.mixin.BooleanFilterMixin;
import net.avdw.todo.core.mixin.IndexSpecificationMixin;
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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

@Command(name = "size", resourceBundle = "messages", description = "${bundle:size.desc}", mixinStandardHelpOptions = true)
public class SizeCli implements Runnable {
    @Inject private HasSize hasSize;
    @Mixin private IndexSpecificationMixin indexSpecificationMixin;
    @Mixin private BooleanFilterMixin booleanFilterMixin;
    @Spec private CommandSpec spec;
    @Inject private Repository<Integer, Todo> todoRepository;
    @Option(names = "--assign", descriptionKey = "size.type.desc")
    private Integer size;
    @Inject private TodoView todoView;
    @Inject private TodoTextCleaner todoTextCleaner;
    @Inject private SizeMapper sizeMapper;
    @Inject private SizeCleaner sizeCleaner;
    @Inject private SizeGroup sizeGroup;
    private final Random random = new Random();

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
            spec.commandLine().getOut().println("No todos to size");
        }
        if (size == null) {
            Scanner scanner = new Scanner(System.in);
            todoList.forEach(todo -> {
                spec.commandLine().getOut().println("");
                Map<String, List<Todo>> sizeGroupMap = todoRepository.findAll(hasSize).stream().collect(Collectors.groupingBy(sizeGroup.collector()));
                sizeGroupMap.forEach((key, list) -> spec.commandLine().getOut().println(String.format("     SIZE %2s: %s", key, todoTextCleaner.clean(list.get(random.nextInt(list.size()))))));
                spec.commandLine().getOut().println(String.format("      ASSIGN: %s", todoView.render(todo)));
                String answer;
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
                            String assign = scanner.next();
                            assignSize = Integer.parseInt(assign);
                            notAssigned = false;
                        } catch (NumberFormatException e) {
                            spec.commandLine().getOut().println("Not a number, please enter a number: ");
                            notAssigned = true;
                        }
                    }
                    String clean = sizeCleaner.clean(todo);
                    Todo newTodo = new Todo(todo.getId(), String.format("%s size:%s", clean, assignSize));
                    todoRepository.update(newTodo);
                    spec.commandLine().getOut().println(todoView.render(newTodo));
                }
            });
        } else {
            todoRepository.setAutoCommit(false);
            todoList.forEach(todo -> {
                String clean = sizeCleaner.clean(todo);
                Todo newTodo = new Todo(todo.getId(), String.format("%s size:%s", clean, size));
                todoRepository.update(newTodo);
                spec.commandLine().getOut().println(todoView.render(newTodo));
            });
            todoRepository.commit();
        }
    }
}
