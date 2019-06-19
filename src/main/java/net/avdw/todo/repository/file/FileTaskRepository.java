package net.avdw.todo.repository.file;

import com.google.inject.Inject;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.model.ATask;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

public class FileTaskRepository implements ARepository<ATask> {
    private Path path;

    @Inject
    public FileTaskRepository(@FileTask Path path) {
        this.path = path;
    }

    @Override
    public void add(ATask task) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ATask retrieve(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ATask update(ATask aTaskList) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ATask delete(ATask aTaskList) {
        throw new UnsupportedOperationException();
    }
    @Override
    public List<ATask> list() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ATask> list(Predicate<ATask> predicate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveList(List<ATask> list) {
        if (!Files.exists(path.resolve(".todo"))) {
            File file = path.resolve(".todo/todo.txt").toFile();
            try {
                if (file.getParentFile().mkdirs() && file.createNewFile()) {
                    Logger.debug(String.format("%s created", file));
                }
            } catch (IOException e) {
                Logger.error(e);
            }
        }

        throw new UnsupportedOperationException();
    }
}
