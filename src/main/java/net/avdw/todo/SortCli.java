package net.avdw.todo;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.todo.repository.Any;
import net.avdw.todo.domain.IsPriority;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Command(name = "sort", resourceBundle = "messages", description = "${bundle:sort}")
public class SortCli implements Runnable {
    private final Gson gson = new Gson();
    @Parameters(description = "Add these keys together to sort by", split = ",", arity = "0..1")
    private List<String> sortKeys;
    @Spec
    private CommandSpec spec;
    @Inject
    private TemplatedResourceBundle templatedResourceBundle;
    @Inject
    private Path todoPath;
    @Inject
    private Repository<Integer, Todo> todoRepository;

    @Override
    public void run() {
        Repository<Integer, Todo> sortedRepository = new FileRepository<>(todoPath, new TodoFileTypeBuilder());
        sortedRepository.setAutoCommit(false);
        sortedRepository.removeAll(new Any<>());
        if (sortKeys == null) {
            sortedRepository.addAll(todoRepository.findAll(new Any<>()).stream()
                    .sorted(Comparator.comparing(Todo::getText))
                    .collect(Collectors.toList()));
        } else {
            sortedRepository.addAll(todoRepository.findAll(new IsPriority()).stream()
                    .sorted(Comparator.comparing(Todo::getText))
                    .collect(Collectors.toList()));
            sortedRepository.addAll(todoRepository.findAll(new Any<Integer, Todo>().not(new IsPriority())).stream()
                    .sorted(Comparator.comparingInt((Todo todo) ->
                            sortKeys.stream().mapToInt(key -> Integer.parseInt(todo.getKey(key).orElse("0"))).sum())
                            .reversed())
                    .collect(Collectors.toList()));
        }
        sortedRepository.commit();

        sortedRepository.findAll(new Any<>()).forEach(todo ->
                spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.TODO_LINE_ITEM,
                        gson.fromJson(String.format("{idx:'%3s',todo:'%s'}", todo.getIdx(), todo), Map.class)))
        );
    }
}