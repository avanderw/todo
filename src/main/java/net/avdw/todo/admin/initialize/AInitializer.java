package net.avdw.todo.admin.initialize;

import java.nio.file.Path;

public interface AInitializer {
    void init();

    void init(Path path);
}
