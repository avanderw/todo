package net.avdw.todo.core;

import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.style.TodoStyler;
import net.avdw.todo.domain.IsDone;
import net.avdw.todo.domain.IsParked;
import net.avdw.todo.domain.IsPriority;
import net.avdw.todo.domain.IsRemoved;
import net.avdw.todo.domain.Priority;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Command(name = "pri", resourceBundle = "messages", description = "${bundle:priority}", mixinStandardHelpOptions = true)
public class PriorityCli implements Runnable {
    private final TemplatedResource templatedResource;
    private final Repository<Integer, Todo> todoRepository;
    private final TodoStyler todoStyler;
    @Option(names = {"-c", "--collapse"}, descriptionKey = "priority.collapse")
    private boolean collapse;
    @Parameters(descriptionKey = "priority.idx.list", arity = "0..1", split = ",", index = "0")
    private List<Integer> idxList;
    @Parameters(descriptionKey = "priority.priority", arity = "0..1")
    private Priority priority;
    @Option(names = {"-r", "--remove"}, descriptionKey = "priority.remove")
    private boolean remove;
    @Option(names = {"-R", "--REMOVE"}, descriptionKey = "priority.clear")
    private boolean removeAll;
    @Spec private CommandSpec spec;

    @Inject
    PriorityCli(final TemplatedResource templatedResource, final Repository<Integer, Todo> todoRepository, final TodoStyler todoStyler) {
        this.templatedResource = templatedResource;
        this.todoRepository = todoRepository;
        this.todoStyler = todoStyler;
    }

    private Priority nextPriority(final List<Priority> priorityList) {
        if (priorityList.size() == 1) {
            return priorityList.get(0);
        } else {
            return priorityList.remove(0);
        }
    }

    @Override
    public void run() {
        if (collapse) {
            Logger.debug("Collapse priority todos");
            final List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
            if (priorityTodoList.isEmpty()) {
                spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.NO_PRIORITY_TODO));
            } else {
                final Map<Priority, Priority> mapping = new HashMap<>();
                final List<Priority> usedPriorityList = priorityTodoList.stream().map(Todo::getPriority).sorted(Enum::compareTo).collect(Collectors.toList());
                final List<Priority> availablePriorityList = new ArrayList<>(Arrays.asList(Priority.values()));
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
                    final Priority pri = todo.getPriority();
                    final Todo updatedTodo = new Todo(todo.getId(), todo.getText().replaceFirst(String.format("\\(%s\\)", pri.name()), String.format("(%s)", mapping.get(pri))));
                    todoRepository.update(updatedTodo);
                    spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                            String.format("{idx:'%3s',todo:\"%s\"}", updatedTodo.getIdx(), todoStyler.style(updatedTodo).replaceAll("\"", "\\\\\""))));
                });
                todoRepository.commit();
            }
            return;
        }

        if (removeAll) {
            Logger.debug("Clear priority todos");
            todoRepository.setAutoCommit(false);
            final List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
            priorityTodoList.forEach(todo -> {
                final Todo updatedTodo = new Todo(todo.getId(), todo.getText().replaceFirst("\\([A-Z]\\) ", ""));
                todoRepository.update(updatedTodo);
                spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                        String.format("{idx:'%3s',todo:\"%s\"}", updatedTodo.getIdx(), todoStyler.style(updatedTodo).replaceAll("\"", "\\\\\""))));
            });
            todoRepository.commit();
            return;
        }

        if (idxList == null) {
            Logger.debug("No index list provided");
            final List<Todo> priorityTodoList = todoRepository.findAll(new IsPriority());
            if (priorityTodoList.isEmpty()) {
                spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.NO_PRIORITY_TODO));
            } else {
                priorityTodoList.forEach(todo -> spec.commandLine().getOut().println(templatedResource.populateKey(
                        ResourceBundleKey.TODO_LINE_ITEM,
                        String.format("{idx:'%3s',todo:\"%s\"}", todo.getIdx(), todoStyler.style(todo).replaceAll("\"", "\\\\\"")))));

            }
        } else {
            Logger.trace("Index list provided");
            if (remove) {
                todoRepository.setAutoCommit(false);
                idxList.forEach(idx -> {
                    final int id = idx - 1;
                    final Todo todoById = todoRepository.findById(id).orElseThrow();
                    final Todo removePriorityTodo = new Todo(todoById.getId(), todoById.getText().replaceFirst("\\([A-Z]\\) ", ""));
                    todoRepository.update(removePriorityTodo);
                    spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                            String.format("{idx:'%3s',todo:\"%s\"}", removePriorityTodo.getIdx(), todoStyler.style(removePriorityTodo).replaceAll("\"", "\\\\\""))));
                });
                todoRepository.commit();
                return;
            }

            final List<Priority> availablePriorityList = new ArrayList<>();
            if (priority == null) {
                Logger.trace("Assume priority for todos");
                final List<Priority> usedPriorityList = todoRepository.findAll(new IsPriority()).stream().map(Todo::getPriority).collect(Collectors.toList());
                availablePriorityList.addAll(Arrays.asList(Priority.values()));
                availablePriorityList.removeAll(usedPriorityList);
                availablePriorityList.sort(Comparator.comparing(Enum::name));
            } else {
                availablePriorityList.add(priority);
            }

            Logger.trace("Assign priority to todos");
            todoRepository.setAutoCommit(false);
            idxList.forEach(idx -> {
                final int id = idx - 1;
                final Todo todoById = todoRepository.findById(id).orElseThrow();
                if (new IsDone().isSatisfiedBy(todoById)) {
                    spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.PRIORITY_NOT_ALLOWED_DONE));
                } else if (new IsRemoved().isSatisfiedBy(todoById)) {
                    spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.PRIORITY_NOT_ALLOWED_REMOVED));
                } else if (new IsParked().isSatisfiedBy(todoById)) {
                    spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.PRIORITY_NOT_ALLOWED_PARKED));
                } else {
                    final String priorityTodoText;
                    final Todo priorityTodo;
                    if (new IsPriority().isSatisfiedBy(todoById)) {
                        priorityTodoText = todoById.getText().replaceFirst("\\([A-Z]\\) ", "");
                        priorityTodo = new Todo(id, String.format("(%s) %s", priority == null ? todoById.getPriority().promote() : priority, priorityTodoText));
                    } else {
                        priorityTodoText = todoById.getText();
                        priorityTodo = new Todo(id, String.format("(%s) %s", nextPriority(availablePriorityList), priorityTodoText));
                    }

                    todoRepository.update(priorityTodo);
                    spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                            String.format("{idx:'%3s',todo:\"%s\"}", idx, todoStyler.style(priorityTodo).replaceAll("\"", "\\\\\""))));
                }
            });
            todoRepository.commit();
        }
    }
}
