package net.avdw.todo.repository.memory;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.file.FileTask;
import net.avdw.todo.repository.model.ATask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MemoryTaskRepository implements ARepository<ATask> {
    private final List<ATask> taskList = new ArrayList<>();

    @Inject
    MemoryTaskRepository(@FileTask ARepository<ATask> fileTaskRepository) {
        taskList.addAll(fileTaskRepository.list());
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
    public void add(ATask task) {
        taskList.add(task);
    }

    @Override
    public List<ATask> list() {
        return ImmutableList.copyOf(taskList);
    }

    @Override
    public List<ATask> list(Predicate<ATask> predicate) {
        return taskList.stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public void saveList(List<ATask> list) {
        throw new UnsupportedOperationException();
    }
}
