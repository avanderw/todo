package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.style.StyleApplicator;
import picocli.CommandLine.Command;
import picocli.CommandLine.IExitCodeGenerator;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.ArrayList;
import java.util.List;

@Command(name = "edit", resourceBundle = "messages", description = "${bundle:edit}")
public class EditCli implements Runnable, IExitCodeGenerator {
    @Option(names = {"-a", "--add"}, descriptionKey = "edit.add")
    private List<String> addStringList = new ArrayList<>();
    private int exitCode = 0;
    @Parameters(descriptionKey = "edit.idx.list", arity = "1", split = ",")
    private List<Integer> idxList;
    @Option(names = {"-r", "--remove"}, descriptionKey = "edit.remove")
    private List<String> removeStringList = new ArrayList<>();
    @Spec
    private CommandSpec spec;
    @Inject
    private TemplatedResource templatedResource;
    @Inject
    private Repository<Integer, Todo> todoRepository;
    @Inject
    private StyleApplicator styleApplicator;

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
            int id = idx - 1;
            String todoText = todoRepository.findById(id).orElseThrow().getText();
            for (String rm : removeStringList) {
                todoText = todoText.replace(rm, "");
            }

            for (String add : addStringList) {
                todoText = String.format("%s %s", todoText, add);
            }

            todoText = todoText.replaceAll("\\s+", " ");
            Todo todo = new Todo(id, todoText);
            todoRepository.update(todo);
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                    String.format("{idx:'%3s',todo:\"%s\"}", idx, styleApplicator.apply(todo.getText()).replaceAll("\"", "\\\\\""))));
        });
        todoRepository.commit();
    }
}
