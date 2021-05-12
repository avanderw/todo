package net.avdw.todo.extension.replace;

import lombok.SneakyThrows;
import net.avdw.todo.TemplatedResource;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;

@Command(name = "replace", resourceBundle = "messages", description = "${bundle:replace.desc}", mixinStandardHelpOptions = true)
public class ReplaceCli implements Runnable {
    private final TemplatedResource templatedResource;
    private final Path todoPath;
    @Parameters(arity = "1", index = "0") private String from;
    @Spec private CommandSpec spec;
    @Parameters(arity = "1", index = "1") private String to;

    @Inject
    ReplaceCli(final TemplatedResource templatedResource, final Path todoPath) {
        this.templatedResource = templatedResource;
        this.todoPath = todoPath;
    }

    @SneakyThrows
    @Override
    public void run() {
        final Path parent = todoPath.getParent();
        if (parent == null) {
            throw new UnsupportedOperationException();
        }
        final Path donePath = parent.resolve("done.txt");
        final Path parkedPath = parent.resolve("parked.txt");
        final Path removedPath = parent.resolve("removed.txt");

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
