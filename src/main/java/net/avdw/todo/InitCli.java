package net.avdw.todo;

import com.google.gson.Gson;
import com.google.inject.Inject;
import lombok.SneakyThrows;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Command(name = "init", resourceBundle = "messages", description = "${bundle:init}")
public class InitCli implements Runnable {
    private final Gson gson = new Gson();
    @Inject
    private Path todoPath;
    @Spec
    private CommandSpec spec;
    @Inject
    private TemplatedResourceBundle templatedResourceBundle;

    @SneakyThrows
    @Override
    public void run() {
        if (Files.exists(todoPath)) {
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.INIT_FILE_EXISTS,
                    gson.fromJson(String.format("{path:'%s'}", todoPath.toUri()), Map.class)));
            return;
        }

        Files.createDirectories(todoPath.getParent());
        Files.createFile(todoPath);
        spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.INIT_FILE_CREATED,
                gson.fromJson(String.format("{path:'%s',usage:'%s'}", todoPath.toUri(), spec.commandLine().getParent().getUsageMessage()), Map.class)));
    }
}