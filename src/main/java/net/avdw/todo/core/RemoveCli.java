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

@Command(name = "rm", resourceBundle = "messages", description = "${bundle:remove}")
public class RemoveCli implements Runnable {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final TemplatedResource templatedResource;
    private final Repository<Integer, Todo> todoRepository;
    private final TodoStyler todoStyler;
    @Parameters(description = "${bundle:remove.idx.list}", arity = "1", split = ",")
    private Set<Integer> idxList;
    @Spec private CommandSpec spec;

    @Inject
    RemoveCli(final TemplatedResource templatedResource, final Repository<Integer, Todo> todoRepository, final TodoStyler todoStyler) {
        this.templatedResource = templatedResource;
        this.todoRepository = todoRepository;
        this.todoStyler = todoStyler;
    }

    @Override
    public void run() {
        todoRepository.setAutoCommit(false);
        idxList.stream().sorted(Comparator.reverseOrder())
                .forEachOrdered(idx -> {
                    final int id = idx - 1;
                    todoRepository.update(new Todo(id, String.format("r %s %s",
                            simpleDateFormat.format(new Date()),
                            todoRepository.findById(id).orElseThrow().toString().replaceFirst("\\([A-Z]\\) ", ""))));
                    spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                            String.format("{idx:'%3s',todo:\"%s\"}", idx, todoStyler.style(todoRepository.findById(id).orElseThrow()).replaceAll("\"", "\\\\\""))));
                });
        todoRepository.commit();
    }
}
