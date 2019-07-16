package net.avdw.todo.repository;

import java.nio.file.Path;

public interface ARepository {
    boolean exists();

    Path getPath();
}
