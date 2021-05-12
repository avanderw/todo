package net.avdw.todo.core;

import lombok.SneakyThrows;
import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.SuppressFBWarnings;
import net.avdw.todo.TemplatedResource;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Command(name = "init", resourceBundle = "messages", description = "${bundle:init}")
public class InitCli implements Runnable {
    private final TemplatedResource templatedResource;
    @Spec
    private CommandSpec spec;

    @Inject
    InitCli(final TemplatedResource templatedResource) {
        this.templatedResource = templatedResource;
    }


    @SneakyThrows
    @Override
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
            justification = "Google Guice does not allow for null injection (todoPath)")
    public void run() {
        final Path todoPath = Paths.get(".todo/todo.txt");
        spec.commandLine().getOut().println("ss");
        if (Files.exists(todoPath)) {
            spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.INIT_FILE_EXISTS,
                    String.format("{path:'%s'}", todoPath.toUri())));
            return;
        }

        Files.createDirectories(todoPath.getParent());
        Files.createFile(todoPath);
        spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.INIT_FILE_CREATED,
                String.format("{path:'%s',usage:'%s'}", todoPath.toUri(), spec.commandLine().getParent().getUsageMessage())));
    }
}
