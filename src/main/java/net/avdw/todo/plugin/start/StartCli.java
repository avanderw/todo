package net.avdw.todo.plugin.start;

import com.google.inject.Inject;
import net.avdw.todo.core.TodoView;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Command(name = "start", resourceBundle = "messages", description = "${bundle:start.desc}", mixinStandardHelpOptions = true)
public class StartCli implements Runnable {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Parameters(descriptionKey = "start.desc.idx.list", arity = "1", split = ",")
    private Set<Integer> idxList;
    @Spec private CommandSpec spec;
    @Inject private Repository<Integer, Todo> todoRepository;
    @Inject private TodoView todoView;

    @Override
    public void run() {
        todoRepository.setAutoCommit(false);
        idxList.forEach(idx -> {
            int id = idx - 1;
            Todo todo = new Todo(id, String.format("%s started:%s",
                    todoRepository.findById(id).orElseThrow(),
                    simpleDateFormat.format(new Date())));
            todoRepository.update(todo);
            spec.commandLine().getOut().println(todoView.render(todo));
        });
        todoRepository.commit();
    }
}
