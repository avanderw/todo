package net.avdw.todo.extension.recur;

import com.google.inject.Inject;
import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.DoneCli;
import net.avdw.todo.core.style.TodoStyler;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.PostAddon;
import net.avdw.todo.extension.due.DueTodoTxtExt;
import net.avdw.todo.repository.Repository;
import org.tinylog.Logger;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RecurDonePostAddon implements PostAddon {
    private final Repository<Integer, Todo> todoRepository;
    private final RecurTodoTxtExt recurTodoTxtExt;
    private final DueTodoTxtExt dueTodoTxtExt;
    private final TemplatedResource templatedResource;
    private final TodoStyler todoStyler;

    @Inject
    public RecurDonePostAddon(final Repository<Integer, Todo> todoRepository, final RecurTodoTxtExt recurTodoTxtExt, final DueTodoTxtExt dueTodoTxtExt, final TemplatedResource templatedResource, final TodoStyler todoStyler) {
        this.todoRepository = todoRepository;
        this.recurTodoTxtExt = recurTodoTxtExt;
        this.dueTodoTxtExt = dueTodoTxtExt;
        this.templatedResource = templatedResource;
        this.todoStyler = todoStyler;
    }

    @Override
    public void process(final List<Todo> todoList, final PrintWriter out) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<String> recurItems = todoList.stream()
                .filter(recurTodoTxtExt::isSatisfiedBy)
                .map(todo -> {
                    RecurDuration recurDuration = recurTodoTxtExt.getValue(todo).orElseThrow();

                    Date dueDate;
                    if (recurDuration.isStrict()) {
                        if (dueTodoTxtExt.isSatisfiedBy(todo)) {
                            dueDate = dueTodoTxtExt.getValue(todo).orElseThrow();
                        } else {
                            Logger.warn("A strict recurring todo does not have a due date\n" +
                                    "{}\n" +
                                    "Using done date instead", todo);
                            dueDate = todo.getDoneDate();
                        }
                    } else {
                        dueDate = todo.getDoneDate();
                    }
                    dueDate = recurDuration.recurFrom(dueDate);

                    String txt = todo.getText().replaceAll("due:\\d\\d\\d\\d-\\d\\d-\\d\\d", "");
                    txt = txt.replaceAll("\\s\\s", " ");
                    txt += String.format(" due:%s", dateFormat.format(dueDate));
                    return txt;
                }).collect(Collectors.toList());

        if (!recurItems.isEmpty()) {
            out.println("Adding recurring item(s):");
        }
        recurItems.forEach(item->{
            Todo todo = new Todo(todoRepository.size(), item);
            todoRepository.add(todo);
            out.println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                    String.format("{idx:'%3s',todo:\"%s\"}", todo.getIdx(), todoStyler.style(todo).replaceAll("\"", "\\\\\""))));
        });
    }
}
