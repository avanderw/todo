package net.avdw.todo;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Command(name = "park", description = "${bundle:park}")
public class ParkCli implements Runnable {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    @Parameters(description = "${bundle:park.idx.list}", arity = "1", split = ",")
    private Set<Integer> idxList;
    @Inject
    private Path todoPath;
    @Spec
    private CommandSpec spec;
    @Inject
    private Repository<Integer, Todo> todoRepository;
    @Inject
    private TemplatedResourceBundle templatedResourceBundle;
    private Gson gson = new Gson();

    @Override
    public void run() {
        todoRepository.setAutoCommit(false);
        idxList.stream().sorted(Comparator.reverseOrder())
                .forEachOrdered(idx -> {
                    int id = idx -1;
                    todoRepository.update(new Todo(id, String.format("p %s %s",
                            SIMPLE_DATE_FORMAT.format(new Date()),
                            todoRepository.findById(id).orElseThrow().toString().replaceFirst("\\([A-Z]\\) ", ""))));
                    spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.TODO_LINE_ITEM,
                            gson.fromJson(String.format("{idx:'%3s',todo:'%s'}", idx, todoRepository.findById(id).orElseThrow()), Map.class)));
                });
        todoRepository.commit();
    }
}
