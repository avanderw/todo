package net.avdw.todo.extension.edit;

import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.style.TodoStyler;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;
import picocli.CommandLine.Command;
import picocli.CommandLine.IExitCodeGenerator;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Command(name = "edit", resourceBundle = "messages", description = "${bundle:edit}")
public class EditCli implements Runnable, IExitCodeGenerator {
    private final TemplatedResource templatedResource;
    private final Repository<Integer, Todo> todoRepository;
    private final TodoStyler todoStyler;
    @Option(names = {"-a", "--add"}, descriptionKey = "edit.add")
    private List<String> addStringList = new ArrayList<>();
    @Option(names = {"-r", "--remove"}, descriptionKey = "edit.remove")
    private List<String> removeStringList = new ArrayList<>();
    private int exitCode = 0;
    @Parameters(descriptionKey = "edit.idx.list", arity = "1", split = ",")
    private List<Integer> idxList;
    @Spec private CommandSpec spec;

    @Inject
    EditCli(final TemplatedResource templatedResource, final Repository<Integer, Todo> todoRepository, final TodoStyler todoStyler) {
        this.templatedResource = templatedResource;
        this.todoRepository = todoRepository;
        this.todoStyler = todoStyler;
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    @Override
    public void run() {
        if (removeStringList.isEmpty() && addStringList.isEmpty()) {
            spec.commandLine().usage(spec.commandLine().getErr());
            exitCode = 1;
            return;
        }

        todoRepository.setAutoCommit(false);
        idxList.forEach(idx -> {
            final int id = idx - 1;
            String todoText = todoRepository.findById(id).orElseThrow().getText();
            for (final String rm : removeStringList) {
                todoText = todoText.replace(rm, "");
            }

            for (final String add : addStringList) {
                todoText = String.format("%s %s", todoText, add);
            }

            todoText = todoText.replaceAll("\\s+", " ");
            final Todo todo = new Todo(id, todoText);
            todoRepository.update(todo);
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                    String.format("{idx:'%3s',todo:\"%s\"}", idx, todoStyler.style(todo).replaceAll("\"", "\\\\\""))));
        });
        todoRepository.commit();
    }
}
