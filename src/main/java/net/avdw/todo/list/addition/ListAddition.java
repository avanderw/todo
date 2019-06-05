package net.avdw.todo.list.addition;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import net.avdw.todo.eventbus.ListUpdatedEvent;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.memory.Memory;
import net.avdw.todo.repository.model.ATask;

public class ListAddition implements AListAddition {
    private final ARepository<ATask> memoryTaskList;
    private final Provider<ATask> taskProvider;
    private final EventBus eventBus;

    @Inject
    ListAddition(@Memory ARepository<ATask> memoryTaskList, Provider<ATask> taskProvider, EventBus eventBus) {
        this.memoryTaskList = memoryTaskList;
        this.taskProvider = taskProvider;
        this.eventBus = eventBus;
    }

    @Override
    public ATask add(String summary) {
        try {
            ATask task = taskProvider.get();
            task.setSummary(summary);
            memoryTaskList.add(task);
            return task;
        } finally {
            eventBus.post(new ListUpdatedEvent());
        }
    }
}
