package net.avdw.todo.plugin.progress;

import com.google.inject.Inject;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.Addon;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;

import java.util.List;

public class ProgressAddon implements Addon {
    private final ProgressExtension progressExtension;
    private final TemplatedResource templatedResource;

    @Inject
    ProgressAddon(final ProgressExtension progressExtension, final TemplatedResource templatedResource) {
        this.progressExtension = progressExtension;
        this.templatedResource = templatedResource;
    }

    @Override
    public String postRender(final List<Todo> list, final Repository<Integer, Todo> repository) {
        return templatedResource.populateKey(ProgressKey.SUMMARY,
                String.format("{subTotal:'%s',total:'%s',todo:'%s',started:'%s',done:'%s'}", list.size(), repository.size(),
                        list.stream().filter(progressExtension::notStarted).count(),
                        list.stream().filter(progressExtension::started).count(),
                        list.stream().filter(Todo::isDone).count()));
    }

    @Override
    public String preRender(final List<Todo> list, final Repository<Integer, Todo> repository) {
        return null;
    }
}
