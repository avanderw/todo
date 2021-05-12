package net.avdw.todo.extension.recur;

import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.style.TodoStyler;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoTextCleaner;
import net.avdw.todo.extension.PostAddon;
import net.avdw.todo.extension.due.DueTodoTxtExt;
import net.avdw.todo.repository.Repository;
import org.tinylog.Logger;

import javax.inject.Inject;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RecurDonePostAddon implements PostAddon {
    private final DueTodoTxtExt dueTodoTxtExt;
    private final RecurTodoTxtExt recurTodoTxtExt;
    private final TemplatedResource templatedResource;
    private final Repository<Integer, Todo> todoRepository;
    private final TodoStyler todoStyler;
    private final TodoTextCleaner todoTextCleaner;

    @Inject
    public RecurDonePostAddon(final Repository<Integer, Todo> todoRepository, final RecurTodoTxtExt recurTodoTxtExt, final DueTodoTxtExt dueTodoTxtExt, final TemplatedResource templatedResource, final TodoStyler todoStyler, final TodoTextCleaner todoTextCleaner) {
        this.todoRepository = todoRepository;
        this.recurTodoTxtExt = recurTodoTxtExt;
        this.dueTodoTxtExt = dueTodoTxtExt;
        this.templatedResource = templatedResource;
        this.todoStyler = todoStyler;
        this.todoTextCleaner = todoTextCleaner;
    }

    @Override
    public void process(final List<Todo> todoList, final PrintWriter out) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final List<String> recurItems = todoList.stream()
                .filter(recurTodoTxtExt::isSatisfiedBy)
                .map(todo -> {
                    final RecurDuration recurDuration = recurTodoTxtExt.getValue(todo).orElseThrow();

                    Date dueDate = null;
                    if (recurDuration.isAsk()) {
                        boolean retry = true;
                        out.printf("  When is '%s' due?%n  ", todoTextCleaner.clean(todo));
                        final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
                        while (retry) {
                            final String input = scanner.next();
                            try {
                                if (Pattern.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d", input)) {
                                    dueDate = dateFormat.parse(input);
                                    retry = false;
                                } else {
                                    throw new UnsupportedOperationException();
                                }
                            } catch (final ParseException | UnsupportedOperationException e) {
                                out.printf("Incorrect date format. Try again using format '%s'%n", dateFormat.toPattern());
                            }
                        }
                    } else {
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
                    }

                    String txt = todo.getText().replaceAll("due:\\d\\d\\d\\d-\\d\\d-\\d\\d", "");
                    txt = txt.replaceAll("\\s\\s", " ");
                    txt += String.format(" due:%s", dateFormat.format(dueDate));
                    return txt;
                }).collect(Collectors.toList());

        if (!recurItems.isEmpty()) {
            out.println("Adding recurring item(s):");
        }
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        todoRepository.setAutoCommit(false);
        recurItems.forEach(item -> {
            item = item.replaceAll("x \\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d\\d\\d-\\d\\d-\\d\\d", simpleDateFormat.format(new Date()));
            final Todo todo = new Todo(todoRepository.size(), item);
            todoRepository.add(todo);
            out.println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                    String.format("{idx:'%3s',todo:\"%s\"}", todo.getIdx(), todoStyler.style(todo).replaceAll("\"", "\\\\\""))));
        });
        todoRepository.commit();
        todoRepository.setAutoCommit(true);
    }
}
