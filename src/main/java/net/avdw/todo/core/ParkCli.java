package net.avdw.todo.core;

import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.style.TodoStyler;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;

@Command(name = "park", resourceBundle = "messages", description = "${bundle:park}")
public class ParkCli implements Runnable {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final TodoStyler todoStyler;
    private final TemplatedResource templatedResource;
    private final Repository<Integer, Todo> todoRepository;
    @Parameters(descriptionKey = "park.idx.list", arity = "1", split = ",")
    private Set<Integer> idxList;
    @Spec private CommandSpec spec;

    @Inject
    ParkCli(final TodoStyler todoStyler, final TemplatedResource templatedResource, final Repository<Integer, Todo> todoRepository) {
        this.todoStyler = todoStyler;
        this.templatedResource = templatedResource;
        this.todoRepository = todoRepository;
    }

    @Override
    public void run() {
        todoRepository.setAutoCommit(false);
        idxList.stream().sorted(Comparator.reverseOrder())
                .forEachOrdered(idx -> {
                    final int id = idx - 1;
                    todoRepository.update(new Todo(id, String.format("p %s %s",
                            simpleDateFormat.format(new Date()),
                            todoRepository.findById(id).orElseThrow().toString().replaceFirst("\\([A-Z]\\) ", ""))));
                    spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                            String.format("{idx:'%3s',todo:\"%s\"}", idx, todoStyler.style(todoRepository.findById(id).orElseThrow()).replaceAll("\"", "\\\\\""))));
                });
        todoRepository.commit();
    }
}
