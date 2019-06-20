package net.avdw.todo.repository.memory;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.avdw.todo.eventbus.RepositorySetEvent;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.model.ATask;

public class MemoryTaskRepositoryEventListener {
    private ARepository<ATask> memoryTaskRepository;

    @Inject
    MemoryTaskRepositoryEventListener(@MemoryTask ARepository<ATask> memoryTaskRepository) {
        this.memoryTaskRepository = memoryTaskRepository;
    }

    @Subscribe
    public void refreshMemoryList(RepositorySetEvent event) {
        memoryTaskRepository.init();
    }
}
