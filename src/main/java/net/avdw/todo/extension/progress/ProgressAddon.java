package net.avdw.todo.extension.progress;

import com.google.inject.Inject;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.extension.Mixin;
import net.avdw.todo.repository.Repository;

import java.util.List;

public class ProgressAddon implements Mixin {
    private final ProgressExt progressExtension;
    private final TemplatedResource templatedResource;

    @Inject
    ProgressAddon(final ProgressExt progressExtension, final TemplatedResource templatedResource) {
        this.progressExtension = progressExtension;
        this.templatedResource = templatedResource;
    }

    @Override
    public String postList(final List<Todo> list, final Repository<Integer, Todo> repository) {
        long done = list.stream().filter(Todo::isDone).count();
        long parked = list.stream().filter(Todo::isParked).count();
        long removed = list.stream().filter(Todo::isRemoved).count();
        long progress = (done + parked + removed) * 100 / list.size();
        return templatedResource.populateKey(ProgressKey.POST_LIST,
                String.format("{subTotal:'%d',total:'%d',todo:'%d',started:'%d',done:'%d',progress:'%d',parked:'%s',removed:'%s'}", list.size(), repository.size(),
                        list.stream().filter(progressExtension::notStarted).count(),
                        list.stream().filter(progressExtension::started).count(),
                        done, progress,
                        (parked == 0) ? "" : parked, (removed == 0) ? "" : removed
                ));
    }

    @Override
    public String postTodo(final Todo todo) {
        return null;
    }

    @Override
    public String preList(final List<Todo> list, final Repository<Integer, Todo> repository) {
        return null;
    }

    @Override
    public String preTodo(final Todo todo) {
        return null;
    }
}
