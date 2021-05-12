package net.avdw.todo.extension.start;

import net.avdw.todo.core.view.TodoView;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Command(name = "start", resourceBundle = "messages", description = "${bundle:start.desc}", mixinStandardHelpOptions = true)
public class StartCli implements Runnable {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final Repository<Integer, Todo> todoRepository;
    private final TodoView todoView;
    @Parameters(descriptionKey = "start.desc.idx.list", arity = "1", split = ",")
    private Set<Integer> idxList;
    @Spec private CommandSpec spec;

    @Inject
    StartCli(final Repository<Integer, Todo> todoRepository, final TodoView todoView) {
        this.todoRepository = todoRepository;
        this.todoView = todoView;
    }

    @Override
    public void run() {
        todoRepository.setAutoCommit(false);
        idxList.forEach(idx -> {
            final int id = idx - 1;
            final Todo todo = new Todo(id, String.format("%s started:%s",
                    todoRepository.findById(id).orElseThrow(),
                    simpleDateFormat.format(new Date())));
            todoRepository.update(todo);
            spec.commandLine().getOut().println(todoView.render(todo));
        });
        todoRepository.commit();
    }
}
