package net.avdw.todo.admin.initialize;

import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileInitializer implements AInitializer {
    @Override
    public void init() {
        initialize(Paths.get("."));
    }

    @Override
    public void init(Path path) {
        initialize(path);
    }

    private void initialize(Path path) {
        if (Files.exists(path.resolve(".todo"))) {
            Logger.warn("Repository already initialized");
        }

        File file = path.resolve(".todo/todo.txt").toFile();
        try {
            if (file.getParentFile().mkdirs() && file.createNewFile()) {
                Logger.debug(String.format("%s created", file));
            }
        } catch (IOException e) {
            Logger.error(e);
        }
    }
}
