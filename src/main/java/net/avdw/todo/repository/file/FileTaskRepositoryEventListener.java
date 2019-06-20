package net.avdw.todo.repository.file;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.avdw.todo.eventbus.ListUpdatedEvent;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.memory.MemoryTask;
import net.avdw.todo.repository.model.AItem;
import net.avdw.todo.repository.model.ATask;

class FileTaskRepositoryEventListener {
    private final ARepository<ATask> memoryRepository;
    private final ARepository<ATask> fileRepository;

    @Inject
    FileTaskRepositoryEventListener(@MemoryTask ARepository<ATask> memoryRepository, @FileTask ARepository<ATask> fileRepository) {
        this.memoryRepository = memoryRepository;
        this.fileRepository = fileRepository;
    }

    @Subscribe
    public void saveUpdate(ListUpdatedEvent listUpdatedEvent) {
        fileRepository.saveList(memoryRepository.list());
    }
}
