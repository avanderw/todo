package net.avdw.todo.extension.replace;

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
        Path donePath = todoPath.getParent().resolve("done.txt");
        Path parkedPath = todoPath.getParent().resolve("parked.txt");
        Path removedPath = todoPath.getParent().resolve("removed.txt");

        Files.writeString(todoPath, Files.readString(todoPath).replaceAll(from, to));
        if (donePath.toFile().exists()) {
            Files.writeString(donePath, Files.readString(donePath).replaceAll(from, to));
        }
        if (parkedPath.toFile().exists()) {
            Files.writeString(parkedPath, Files.readString(parkedPath).replaceAll(from, to));
        }
        if (removedPath.toFile().exists()) {
            Files.writeString(removedPath, Files.readString(removedPath).replaceAll(from, to));
        }

        spec.commandLine().getOut().println(templatedResource.populateKey(ReplaceKey.SUCCESS,
                String.format("{from:'%s',to:'%s'}", from, to)));
    }
}
