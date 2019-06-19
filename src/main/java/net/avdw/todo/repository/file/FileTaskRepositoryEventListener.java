package net.avdw.todo.repository.file;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.avdw.todo.eventbus.ListUpdatedEvent;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.memory.MemoryTask;
import net.avdw.todo.repository.model.AItem;

class FileTaskRepositoryEventListener<T extends AItem> {
    private final ARepository<T> memoryRepository;
    private final ARepository<T> fileRepository;

    @Inject
    FileTaskRepositoryEventListener(@MemoryTask ARepository<T> memoryRepository, @FileTask ARepository<T> fileRepository) {
        this.memoryRepository = memoryRepository;
        this.fileRepository = fileRepository;
    }

    @Subscribe
    public void saveUpdate(ListUpdatedEvent listUpdatedEvent) {
        fileRepository.saveList(memoryRepository.list());
    }
}
