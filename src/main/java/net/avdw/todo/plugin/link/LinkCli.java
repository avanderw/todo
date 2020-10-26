package net.avdw.todo.plugin.link;

import com.google.inject.Inject;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.mixin.IndexSpecificationMixin;
import net.avdw.todo.core.view.TodoView;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.util.Optional;

@Command(name = "link", resourceBundle = "messages", description = "${bundle:link.desc}", mixinStandardHelpOptions = true)
public class LinkCli implements Runnable {
    @Mixin private IndexSpecificationMixin childrenMixin;
    @Inject private LinkExt linkExt;
    @Parameters(arity = "1", index = "1", descriptionKey = "link.parent.desc") private Integer parentIdx;
    @Spec private CommandSpec spec;
    @Inject private Repository<Integer, Todo> todoRepository;
    @Inject private TodoView todoView;
    @Inject private TemplatedResource templatedResource;

    @Override
    public void run() {
        Todo parentTodo = todoRepository.findById(parentIdx - 1).orElseThrow();
        String parentLink;
        Todo updatedParentTodo;
        if (linkExt.getValueList(parentTodo).isEmpty()) {
            int maxLink = todoRepository.findAll(linkExt).stream()
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
        spec.commandLine().getOut().println(templatedResource.populateKey(LinkKey.PARENT_HEADER));
        todoRepository.update(updatedParentTodo);
        spec.commandLine().getOut().println(todoView.render(updatedParentTodo));

        spec.commandLine().getOut().println(templatedResource.populateKey(LinkKey.CHILDREN_HEADER));
        for (Todo childTodo : todoRepository.findAll(childrenMixin)) {
            int maxLink = todoRepository.findAll(linkExt).stream()
                    .map(linkExt::getValue)
                    .filter(Optional::isPresent)
                    .map(Optional::orElseThrow)
                    .filter(link -> link.length() > parentLink.length())
                    .filter(link -> link.startsWith(parentLink))
                    .map(link -> link.substring(link.lastIndexOf(".") + 1))
                    .mapToInt(Integer::parseInt)
                    .max().orElse(0);

            Todo updatedChildTodo = new Todo(childTodo.getId(),
                    String.format("%s link:%s.%d",
                            childTodo.getText().replaceAll(String.format("\\s(%s):\\S+", String.join("|", linkExt.getSupportedExtList())), ""),
                            parentLink,
                            maxLink + 1));
            todoRepository.update(updatedChildTodo);
            spec.commandLine().getOut().println(todoView.render(updatedChildTodo));
        }
    }
}
