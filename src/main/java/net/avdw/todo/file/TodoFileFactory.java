package net.avdw.todo.file;

import java.nio.file.Path;

public interface TodoFileFactory {
    TodoFile create(Path path);
}
