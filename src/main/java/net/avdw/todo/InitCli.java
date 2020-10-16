package net.avdw.todo;

import com.google.inject.Inject;
import lombok.SneakyThrows;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.nio.file.Files;
import java.nio.file.Path;

@Command(name = "init", resourceBundle = "messages", description = "${bundle:init}")
public class InitCli implements Runnable {
    @Inject
    private Path todoPath;
    @Spec
    private CommandSpec spec;
    @Inject
    private TemplatedResource templatedResource;

    @SneakyThrows
    @Override
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
            justification = "Google Guice does not allow for null injection (todoPath)")
    public void run() {
        if (Files.exists(todoPath)) {
            spec.commandLine().getOut().println(templatedResource.populate(ResourceBundleKey.INIT_FILE_EXISTS,
                    String.format("{path:'%s'}", todoPath.toUri())));
            return;
        }

        Files.createDirectories(todoPath.getParent());
        Files.createFile(todoPath);
        spec.commandLine().getOut().println(templatedResource.populate(ResourceBundleKey.INIT_FILE_CREATED,
                String.format("{path:'%s',usage:'%s'}", todoPath.toUri(), spec.commandLine().getParent().getUsageMessage())));
    }
}
