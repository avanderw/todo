package net.avdw.todo.core;

import com.google.inject.Inject;
import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.style.TodoStyler;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;

@Command(name = "park", resourceBundle = "messages", description = "${bundle:park}")
public class ParkCli implements Runnable {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Parameters(descriptionKey = "park.idx.list", arity = "1", split = ",")
    private Set<Integer> idxList;
    @Spec private CommandSpec spec;
    @Inject private TodoStyler todoStyler;
    @Inject private TemplatedResource templatedResource;
    @Inject private Repository<Integer, Todo> todoRepository;

    @Override
    public void run() {
        todoRepository.setAutoCommit(false);
        idxList.stream().sorted(Comparator.reverseOrder())
                .forEachOrdered(idx -> {
                    int id = idx - 1;
                    todoRepository.update(new Todo(id, String.format("p %s %s",
                            simpleDateFormat.format(new Date()),
                            todoRepository.findById(id).orElseThrow().toString().replaceFirst("\\([A-Z]\\) ", ""))));
                    spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                            String.format("{idx:'%3s',todo:\"%s\"}", idx, todoStyler.style(todoRepository.findById(id).orElseThrow()).replaceAll("\"", "\\\\\""))));
                });
        todoRepository.commit();
    }
}