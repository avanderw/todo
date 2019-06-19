package net.avdw.todo.list.filtering;

import com.google.inject.Inject;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.memory.MemoryTask;
import net.avdw.todo.repository.model.ATask;

import java.util.List;
import java.util.stream.Collectors;

public class ContextFilter implements AFilter {
    private final ARepository<ATask> memoryRepository;

    @Inject
    ContextFilter(@MemoryTask ARepository<ATask> memoryRepository) {
        this.memoryRepository = memoryRepository;
    }

    @Override
    public List<String> list() {
        return memoryRepository.list().stream().flatMap(task->task.getContexts().stream()).collect(Collectors.toList());
    }

    @Override
    public List<String> list(List<String> filters) {
        throw new UnsupportedOperationException();
    }
}
