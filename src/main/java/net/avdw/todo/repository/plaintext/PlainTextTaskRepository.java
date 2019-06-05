package net.avdw.todo.repository.plaintext;

import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.model.ATask;

import java.util.List;
import java.util.function.Predicate;

public class PlainTextTaskRepository implements ARepository<ATask> {

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
        throw new UnsupportedOperationException();
    }
}
