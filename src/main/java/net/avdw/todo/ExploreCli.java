package net.avdw.todo;

import com.google.inject.Inject;
import lombok.SneakyThrows;
import picocli.CommandLine.Command;

import java.nio.file.Files;
import java.nio.file.Path;

@Command(name = "explore", resourceBundle = "messages", description = "${bundle:explore}")
public class ExploreCli implements Runnable {
    @Inject
    private Path todoPath;

    @Override
    @SneakyThrows
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
            justification = "Google Guice does not allow for null injection (todoPath)")
    public void run() {
        if (Files.exists(todoPath)) {
            ProcessBuilder pb = new ProcessBuilder("explorer.exe", todoPath.getParent().toString());
            pb.start();
        }
    }
}
