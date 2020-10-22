package net.avdw.todo.plugin.replace;

import com.google.inject.Inject;
import lombok.SneakyThrows;
import net.avdw.todo.TemplatedResource;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.nio.file.Files;
import java.nio.file.Path;

@Command(name = "replace", resourceBundle = "messages", description = "${bundle:replace.desc}", mixinStandardHelpOptions = true)
public class ReplaceCli implements Runnable {
    @Parameters(arity = "1", index = "0") private String from;
    @Spec private CommandSpec spec;
    @Inject private TemplatedResource templatedResource;
    @Parameters(arity = "1", index = "1") private String to;
    @Inject private Path todoPath;

    @SneakyThrows
    @Override
    public void run() {
        String content = Files.readString(todoPath);
        Files.writeString(todoPath, content.replaceAll(from, to));
        spec.commandLine().getOut().println(templatedResource.populateKey(ReplaceKey.SUCCESS,
                String.format("{from:'%s',to:'%s'}", from, to)));
    }
}
