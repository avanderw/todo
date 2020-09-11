package net.avdw.todo;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.todo.domain.*;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.style.StyleApplicator;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.*;
import java.util.stream.Collectors;

@Command(name = "pri", resourceBundle = "messages", description = "${bundle:priority}")
public class PriorityCli implements Runnable {
    private final Gson gson = new Gson();
    @Parameters(description = "${bundle:priority.idx.list}", arity = "0..1", split = ",", index = "0")
    private List<Integer> idxList;
    @Parameters(description = "${bundle:priority.priority}", arity = "0..1")
    private Priority priority;
    @Option(names = {"-r", "--remove"}, description = "${bundle:priority.remove")
    private boolean remove;
    @Option(names = "--clear", description = "${bundle:priority.clear")
    private boolean clear;
    @Option(names = {"-c", "--collapse"}, description = "${bundle:priority.collapse")
    private boolean collapse;
    @Spec
    private CommandSpec spec;
    @Inject
    private Repository<Integer, Todo> todoRepository;
    @Inject
    private TemplatedResourceBundle templatedResourceBundle;
    @Inject
    private StyleApplicator styleApplicator;

    @Override
    public void run() {
        if (collapse) {
            Logger.debug("Collapse priority todos");
            List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
            if (priorityTodoList.isEmpty()) {
                spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.NO_PRIORITY_TODO));
            } else {
                Map<Priority, Priority> mapping = new HashMap<>();
                List<Priority> usedPriorityList = priorityTodoList.stream().map(Todo::getPriority).collect(Collectors.toList());
                usedPriorityList.sort(Enum::compareTo);
                List<Priority> availablePriorityList = new ArrayList<>(Arrays.asList(Priority.values()));
                availablePriorityList.removeAll(usedPriorityList);
                availablePriorityList.sort(Enum::compareTo);
                usedPriorityList.forEach(p -> {
                    mapping.putIfAbsent(p, p);
                    if (p.compareTo(availablePriorityList.get(0)) > 0) {
                        mapping.put(p, nextPriority(availablePriorityList));
                        availablePriorityList.add(p);
                        availablePriorityList.sort(Enum::compareTo);
                    }
                });
                todoRepository.setAutoCommit(false);
                priorityTodoList.forEach(todo -> {
                    Priority pri = todo.getPriority();
                    Todo updatedTodo = new Todo(todo.getId(), todo.getText().replaceFirst(String.format("\\(%s\\)", pri.name()), String.format("(%s)", mapping.get(pri))));
                    todoRepository.update(updatedTodo);
                    spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.TODO_LINE_ITEM,
                            gson.fromJson(String.format("{idx:'%3s',todo:'%s'}", updatedTodo.getIdx(), styleApplicator.apply(updatedTodo.getText())), Map.class)));
                });
                todoRepository.commit();
            }
            return;
        }

        if (clear) {
            Logger.debug("Clear priority todos");
            todoRepository.setAutoCommit(false);
            List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
            priorityTodoList.forEach(todo -> {
                Todo updatedTodo = new Todo(todo.getId(), todo.getText().replaceFirst("\\([A-Z]\\) ", ""));
                todoRepository.update(updatedTodo);
                spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.TODO_LINE_ITEM,
                        gson.fromJson(String.format("{idx:'%3s',todo:'%s'}", updatedTodo.getIdx(), styleApplicator.apply(updatedTodo.getText())), Map.class)));
            });
            todoRepository.commit();
            return;
        }

        if (idxList == null) {
            Logger.debug("No index list provided");
            List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
            if (priorityTodoList.isEmpty()) {
                spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.NO_PRIORITY_TODO));
            } else {
                priorityTodoList.forEach(todo -> spec.commandLine().getOut().println(templatedResourceBundle.getString(
                        ResourceBundleKey.TODO_LINE_ITEM,
                        gson.fromJson(String.format("{idx:'%3s',todo:'%s'}", todo.getIdx(), styleApplicator.apply(todo.getText())), Map.class))));

            }
        } else {
            Logger.debug("Index list provided");
            if (remove) {
                todoRepository.setAutoCommit(false);
                idxList.forEach(idx -> {
                    int id = idx - 1;
                    Todo todoById = todoRepository.findById(id).orElseThrow();
                    Todo removePriorityTodo = new Todo(todoById.getId(), todoById.getText().replaceFirst("\\([A-Z]\\) ", ""));
                    todoRepository.update(removePriorityTodo);
                    spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.TODO_LINE_ITEM,
                            gson.fromJson(String.format("{idx:'%3s',todo:'%s'}", removePriorityTodo.getIdx(), styleApplicator.apply(removePriorityTodo.getText())), Map.class)));
                });
                todoRepository.commit();
                return;
            }

            List<Priority> availablePriorityList = new ArrayList<>();
            if (priority == null) {
                Logger.debug("Assume priority for todos");
                List<Priority> usedPriorityList = todoRepository.findAll(new IsPriority()).stream().map(Todo::getPriority).collect(Collectors.toList());
                availablePriorityList.addAll(Arrays.asList(Priority.values()));
                availablePriorityList.removeAll(usedPriorityList);
                availablePriorityList.sort(Comparator.comparing(Enum::name));
            } else {
                availablePriorityList.add(priority);
            }

            Logger.debug("Assign priority to todos");
            todoRepository.setAutoCommit(false);
            idxList.forEach(idx -> {
                int id = idx - 1;
                Todo todoById = todoRepository.findById(id).orElseThrow();
                if (new IsDone().isSatisfiedBy(todoById)) {
                    spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.PRIORITY_NOT_ALLOWED_DONE));
                } else if (new IsRemoved().isSatisfiedBy(todoById)) {
                    spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.PRIORITY_NOT_ALLOWED_REMOVED));
                } else if (new IsParked().isSatisfiedBy(todoById)) {
                    spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.PRIORITY_NOT_ALLOWED_PARKED));
                } else {
                    String priorityTodoText;
                    if (new IsPriority().isSatisfiedBy(todoById)) {
                        priorityTodoText = todoById.getText().replaceFirst("\\([A-Z]\\) ", "");
                    } else {
                        priorityTodoText = todoById.getText();
                    }

                    Todo priorityTodo = new Todo(id, String.format("(%s) %s", nextPriority(availablePriorityList), priorityTodoText));
                    todoRepository.update(priorityTodo);
                    spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.TODO_LINE_ITEM,
                            gson.fromJson(String.format("{idx:'%3s',todo:'%s'}", idx, styleApplicator.apply(priorityTodo.getText())), Map.class)));
                }
            });
            todoRepository.commit();
        }
    }

    private Priority nextPriority(final List<Priority> priorityList) {
        if (priorityList.size() == 1) {
            return priorityList.get(0);
        } else {
            return priorityList.remove(0);
        }
    }
}
