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
    public void run() {
        if (Files.exists(todoPath)) {
            ProcessBuilder pb = new ProcessBuilder("explorer.exe", todoPath.getParent().toString());
            pb.start();
        }
    }
}
