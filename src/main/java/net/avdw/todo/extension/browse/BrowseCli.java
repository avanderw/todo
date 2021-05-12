package net.avdw.todo.extension.browse;

import net.avdw.todo.TemplatedResource;
import net.avdw.todo.core.mixin.BooleanFilterMixin;
import net.avdw.todo.core.mixin.DateFilterMixin;
import net.avdw.todo.core.mixin.IndexFilterMixin;
import net.avdw.todo.core.mixin.RepositoryMixin;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Command(name = "browse", resourceBundle = "messages", description = "${bundle:browse.description}", mixinStandardHelpOptions = true)
public class BrowseCli implements Runnable {

    @Option(names = "--done")
    private boolean isDone = false;
    @Option(names = "--parked")
    private boolean isParked = false;
    @Option(names = "--removed")
    private boolean isRemoved = false;
    @Option(names = "--todo")
    private boolean isTodo = false;
    @Option(names = {"--dir", "--directory"})
    private boolean isDirectory = false;
    private final BrowseMapper browseMapper;
    private final BrowseSpecification browseSpecification;
    private final TemplatedResource templatedResource;
    private final Path todoPath;
    @Mixin private BooleanFilterMixin booleanFilterMixin;
    @Mixin private DateFilterMixin dateFilterMixin;
    @Mixin private IndexFilterMixin indexSpecificationMixin;
    @Mixin private RepositoryMixin repositoryMixin;
    @Spec private CommandSpec spec;

    @Inject
    BrowseCli(final BrowseMapper browseMapper, final BrowseSpecification browseSpecification, final TemplatedResource templatedResource, final Path todoPath) {
        this.browseMapper = browseMapper;
        this.browseSpecification = browseSpecification;
        this.templatedResource = templatedResource;
        this.todoPath = todoPath;
    }

    @Override
    public void run() {
        final Repository<Integer, Todo> repository = repositoryMixin.repository();
        final Path parent = todoPath.getParent();
        if (parent == null) {
            throw new UnsupportedOperationException();
        }
        final List<URI> uriList = new ArrayList<>();
        if (isTodo) {
            uriList.add(todoPath.toUri());
        } else if (isDirectory) {
            uriList.add(parent.toUri());
        } else if (isDone) {
            uriList.add(parent.resolve("done.txt").toUri());
        } else if (isRemoved) {
            uriList.add(parent.resolve("removed.txt").toUri());
        } else if (isParked) {
            uriList.add(parent.resolve("parked.txt").toUri());
        } else {
            Specification<Integer, Todo> specification = indexSpecificationMixin;
            specification = specification.and(browseSpecification);
            specification = specification.and(dateFilterMixin.specification());
            specification = specification.and(booleanFilterMixin.specification());
            Logger.debug(specification);
            uriList.addAll(repository.findAll(specification).stream()
                    .flatMap(browseMapper::mapToUriStream)
                    .collect(Collectors.toList()));
        }

        if (Desktop.isDesktopSupported()) {
            if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                uriList.forEach(uri -> {
                    final String json = String.format("{uri:'%s'}", uri);
                    try {
                        Desktop.getDesktop().browse(uri);
                        spec.commandLine().getOut().println(templatedResource.populateKey(BrowseKey.BROWSE_URI, json));
                    } catch (final IOException e) {
                        spec.commandLine().getOut().println(templatedResource.populateKey(BrowseKey.BROWSE_URI_FAIL, json));
                    }
                });
            } else {
                spec.commandLine().getOut().println(templatedResource.populateKey(BrowseKey.NOT_SUPPORTED));
            }
        } else {
            spec.commandLine().getOut().println(templatedResource.populateKey(BrowseKey.NOT_SUPPORTED));
        }
    }
}
