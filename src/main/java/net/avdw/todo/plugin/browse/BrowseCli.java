package net.avdw.todo.plugin.browse;

import com.google.inject.Inject;
import net.avdw.todo.RepositoryMixin;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.filters.BooleanFilterMixin;
import net.avdw.todo.filters.DateFilterMixin;
import net.avdw.todo.filters.IndexSpecificationMixin;
import net.avdw.todo.plugin.Plugin;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Plugin
@Command(name = "browse", resourceBundle = "messages", description = "${bundle:browse.description}", mixinStandardHelpOptions = true)
public class BrowseCli implements Runnable {

    @Mixin private BooleanFilterMixin booleanFilterMixin;
    @Inject private BrowseMapper browseMapper;
    @Inject private BrowseSpecification browseSpecification;
    @Mixin private DateFilterMixin dateFilterMixin;
    @Mixin private IndexSpecificationMixin indexSpecificationMixin;
    @Option(names = "--done")
    private boolean isDone = false;
    @Option(names = "--parked")
    private boolean isParked = false;
    @Option(names = "--removed")
    private boolean isRemoved = false;
    @Option(names = "--todo")
    private boolean isTodo = false;
    @Mixin private RepositoryMixin repositoryMixin;
    @Spec private CommandSpec spec;
    @Inject private TemplatedResource templatedResource;
    @Inject private Path todoPath;

    @Override
    public void run() {
        Repository<Integer, Todo> repository = repositoryMixin.repository();
        List<URI> uriList = new ArrayList<>();
        if (isTodo) {
            uriList.add(todoPath.toUri());
        } else if (isDone) {
            uriList.add(todoPath.getParent().resolve("done.txt").toUri());
        } else if (isRemoved) {
            uriList.add(todoPath.getParent().resolve("removed.txt").toUri());
        } else if (isParked) {
            uriList.add(todoPath.getParent().resolve("parked.txt").toUri());
        } else {
            Specification<Integer, Todo> specification = indexSpecificationMixin;
            specification = specification.and(browseSpecification);
            specification = specification.and(dateFilterMixin.specification());
            specification = specification.and(booleanFilterMixin.specification());
            Logger.debug(specification);
            uriList = repository.findAll(specification).stream()
                    .flatMap(browseMapper::map)
                    .collect(Collectors.toList());
        }

        if (Desktop.isDesktopSupported()) {
            if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                uriList.forEach(uri -> {
                    String json = String.format("{uri:'%s'}", uri);
                    try {
                        Desktop.getDesktop().browse(uri);
                        spec.commandLine().getOut().println(templatedResource.populate(BrowseKey.BROWSE_URI, json));
                    } catch (IOException e) {
                        spec.commandLine().getOut().println(templatedResource.populate(BrowseKey.BROWSE_URI_FAIL, json));
                    }
                });
            } else {
                spec.commandLine().getOut().println(templatedResource.populate(BrowseKey.NOT_SUPPORTED));
            }
        } else {
            spec.commandLine().getOut().println(templatedResource.populate(BrowseKey.NOT_SUPPORTED));
        }
    }
}
