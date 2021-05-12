package net.avdw.todo.extension.dependency;

import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.mixin.IndexFilterMixin;
import net.avdw.todo.core.view.TodoView;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
import java.util.Optional;

@Command(name = "dependency", resourceBundle = "messages", description = "${bundle:link.desc}", mixinStandardHelpOptions = true)
public class DependencyCli implements Runnable {
    private final Repository<Integer, Todo> todoRepository;
    private final TodoView todoView;
    private final TemplatedResource templatedResource;
    private final DependencyExt linkExt;
    @Mixin private IndexFilterMixin childrenMixin;
    @Parameters(arity = "1", index = "1", descriptionKey = "link.parent.desc") private Integer parentIdx;
    @Spec private CommandSpec spec;

    @Inject
    DependencyCli(final Repository<Integer, Todo> todoRepository, final TodoView todoView, final TemplatedResource templatedResource, final DependencyExt linkExt) {
        this.todoRepository = todoRepository;
        this.todoView = todoView;
        this.templatedResource = templatedResource;
        this.linkExt = linkExt;
    }

    @Override
    public void run() {
        final Todo parentTodo = todoRepository.findById(parentIdx - 1).orElseThrow();
        final String parentLink;
        final Todo updatedParentTodo;
        if (linkExt.getValueList(parentTodo).isEmpty()) {
            final int maxLink = todoRepository.findAll(linkExt).stream()
                    .map(linkExt::getValue)
                    .filter(Optional::isPresent)
                    .map(Optional::orElseThrow)
                    .filter(link -> link.length() == 1)
                    .mapToInt(Integer::parseInt)
                    .max().orElse(0);
            parentLink = "" + (maxLink + 1);
            updatedParentTodo = new Todo(parentTodo.getId(), String.format("%s link:%s", parentTodo.getText(), parentLink));
        } else {
            parentLink = linkExt.getValue(parentTodo).orElseThrow();
            updatedParentTodo = parentTodo;
        }
        spec.commandLine().getOut().println(templatedResource.populateKey(DependencyKey.PARENT_HEADER));
        todoRepository.update(updatedParentTodo);
        spec.commandLine().getOut().println(todoView.render(updatedParentTodo));

        spec.commandLine().getOut().println(templatedResource.populateKey(DependencyKey.CHILDREN_HEADER));
        for (final Todo childTodo : todoRepository.findAll(childrenMixin)) {
            final int maxLink = todoRepository.findAll(linkExt).stream()
                    .map(linkExt::getValue)
                    .filter(Optional::isPresent)
                    .map(Optional::orElseThrow)
                    .filter(link -> link.length() > parentLink.length())
                    .filter(link -> link.startsWith(parentLink))
                    .map(link -> link.substring(link.lastIndexOf(".") + 1))
                    .mapToInt(Integer::parseInt)
                    .max().orElse(0);

            final Todo updatedChildTodo = new Todo(childTodo.getId(),
                    String.format("%s link:%s.%d",
                            childTodo.getText().replaceAll(String.format("\\s(%s):\\S+", String.join("|", linkExt.getSupportedExtList())), ""),
                            parentLink,
                            maxLink + 1));
            todoRepository.update(updatedChildTodo);
            spec.commandLine().getOut().println(todoView.render(updatedChildTodo));
        }
    }
}
