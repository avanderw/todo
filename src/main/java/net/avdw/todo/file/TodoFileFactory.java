package net.avdw.todo.file;

import java.nio.file.Path;
@Deprecated
public interface TodoFileFactory {
    TodoFile create(Path path);
}
