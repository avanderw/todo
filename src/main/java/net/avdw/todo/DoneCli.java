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

@Command(name = "do", description = "${bundle:done}")
public class DoneCli implements Runnable {
    private final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    @Parameters(description = "${bundle:done.idx.list}", arity = "1..*")
    private Set<Integer> idxList;
    @Inject
    private Path todoPath;
    @Spec
    private CommandSpec spec;
    @Inject
    private Repository<Todo> todoRepository;
    @Inject
    private TemplatedResourceBundle templatedResourceBundle;
    private Gson gson = new Gson();

    @Override
    public void run() {
        todoRepository.setAutoCommit(false);
        idxList.stream().sorted(Comparator.reverseOrder())
                .forEachOrdered(idx -> {
                    todoRepository.save(idx - 1, new Todo(String.format("x %s %s",
                            SIMPLE_DATE_FORMAT.format(new Date()),
                            todoRepository.findById(idx - 1).toString().replaceFirst("\\([A-Z]\\) ", ""))));
                    spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.TODO_LINE_ITEM,
                            gson.fromJson(String.format("{idx:'%3s',todo:'%s'}", idx, todoRepository.findById(idx - 1)), Map.class)));
                });
        todoRepository.commit();
    }
}
