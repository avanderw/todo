package net.avdw.todo.list.filtering;

import com.google.inject.Inject;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.memory.MemoryTask;
import net.avdw.todo.repository.model.ATask;

import java.util.List;
import java.util.stream.Collectors;

public class TodoListFilter implements AFilter {
    private ARepository<ATask> memoryRepository;

    @Inject
    TodoListFilter(@MemoryTask ARepository<ATask> memoryRepository) {
        this.memoryRepository = memoryRepository;
    }

    @Override
    public List<String> list() {
        return memoryRepository.list().stream().map(ATask::toString).collect(Collectors.toList());
    }

    @Override
    public List<String> list(List<String> filters) {
        return memoryRepository.list(aTask -> filters.stream()
                .allMatch(filter -> aTask.toString().toLowerCase().contains(filter.toLowerCase()))).stream()
                .map(ATask::toString)
                .collect(Collectors.toList());
    }
}
