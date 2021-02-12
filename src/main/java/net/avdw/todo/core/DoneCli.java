package net.avdw.todo.core;

import com.google.inject.Inject;
import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.mixin.BooleanFilterMixin;
import net.avdw.todo.core.mixin.IndexFilterMixin;
import net.avdw.todo.core.style.TodoStyler;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

@Command(name = "do", resourceBundle = "messages", description = "${bundle:done}")
public class DoneCli implements Runnable {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Mixin private BooleanFilterMixin booleanFilterMixin;
    @Mixin private IndexFilterMixin indexFilterMixin;
    @Spec private CommandSpec spec;
    @Inject private TemplatedResource templatedResource;
    @Inject private Repository<Integer, Todo> todoRepository;
    @Inject private TodoStyler todoStyler;

    private List<Todo> filter() {
        Specification<Integer, Todo> specification = new Any<>();
        if (indexFilterMixin.isActive()) {
            specification = indexFilterMixin;
        }

        if (booleanFilterMixin.isActive()) {
            specification = specification.or(booleanFilterMixin);
        }

        return todoRepository.findAll(specification);
    }

    private void operation(final List<Todo> todoList) {
        todoRepository.setAutoCommit(false);
        todoList.forEach(todo -> {
            todoRepository.update(new Todo(todo.getId(), String.format("x %s %s",
                    simpleDateFormat.format(new Date()),
                    todoRepository.findById(todo.getId()).orElseThrow().toString().replaceFirst("\\([A-Z]\\) ", ""))));
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                    String.format("{idx:'%3s',todo:\"%s\"}", todo.getId() + 1, todoStyler.style(todoRepository.findById(todo.getId()).orElseThrow()).replaceAll("\"", "\\\\\""))));
        });
        todoRepository.commit();
    }

    @Override
    public void run() {
        boolean proceed = true;
        if (!indexFilterMixin.isActive() && !booleanFilterMixin.isActive()) {
            Scanner scanner = new Scanner(System.in);
            spec.commandLine().getOut().println("No filters were specified. All items will be marked as done. Proceed (y,n)?");
            String answer = scanner.next();
            proceed = answer.toLowerCase(Locale.ENGLISH).equals("y");
        }

        if (proceed) {
            List<Todo> todoList = filter();
            //pre(todoList);
            operation(todoList);
            //post(todoList);
        } else {
            spec.commandLine().usage(spec.commandLine().getOut());
        }
    }
}
