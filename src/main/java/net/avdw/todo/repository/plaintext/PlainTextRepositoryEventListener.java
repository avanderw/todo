package net.avdw.todo.repository.plaintext;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.avdw.todo.eventbus.ListUpdatedEvent;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.memory.Memory;
import net.avdw.todo.repository.model.AItem;

class PlainTextRepositoryEventListener<T extends AItem> {
    private final ARepository<T> memoryRepository;
    private final ARepository<T> fileRepository;

    @Inject
    PlainTextRepositoryEventListener(@Memory ARepository<T> memoryRepository, @PlainText ARepository<T> fileRepository) {
        this.memoryRepository = memoryRepository;
        this.fileRepository = fileRepository;
    }

    @Subscribe
    public void saveUpdate(ListUpdatedEvent listUpdatedEvent) {
        fileRepository.saveList(memoryRepository.list());
    }
}
